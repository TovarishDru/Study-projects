import pika
import sys
import json


from Globals import RMQ_HOST, RMQ_USER, RMQ_PASS, EXCHANGE_NAME


def callback(channel, method, properties, body):
    try:
        number = int(body.decode('utf-8'))
        square = number ** 3
        channel.basic_publish(
            exchange=EXCHANGE_NAME,
            routing_key='cubed',
            body=str(square)
        )
    except ValueError:
        print(f"Invalid number received: {body}")


def main():
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(
            host=RMQ_HOST,
            credentials=pika.PlainCredentials(RMQ_USER, RMQ_PASS)
        )
    )
    channel = connection.channel()
    channel.exchange_declare(
        exchange=EXCHANGE_NAME,
        exchange_type=pika.exchange_type.ExchangeType.topic,
        durable=True
    )
    queue_name = channel.queue_declare(queue='', exclusive=True).method.queue
    channel.queue_bind(
        exchange=EXCHANGE_NAME,
        queue=queue_name,
        routing_key='original'
    )
    channel.basic_consume(
        queue=queue_name,
        on_message_callback=callback,
        auto_ack=True
    )
    try:
        channel.start_consuming()
    except KeyboardInterrupt:
        pass


if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        print(' [x] Cuber stopped')
        sys.exit(0)
