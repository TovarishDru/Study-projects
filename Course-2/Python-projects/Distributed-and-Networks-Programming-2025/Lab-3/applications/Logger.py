import pika
from datetime import datetime
import json
from Globals import RMQ_HOST, RMQ_USER, RMQ_PASS, EXCHANGE_NAME


LOG_FILE = 'rabbitmq_messages.log'


def setup_logging():
    with open(LOG_FILE, 'w') as f:
        f.write(f"\n\n=== Log started at {datetime.now().isoformat()} ===\n")


def callback(channel, method, properties, body):
    log_data = {
        "sender": "test1",
        "message_type": str(method.routing_key),
        "number": body.decode('utf-8'),
    }
    log_message(log_data)


def log_message(body):
    timestamp = datetime.now().isoformat()
    sender = body["sender"]
    message_type = body["message_type"]
    number = body["number"]
    log_entry = f'''[{timestamp}] {sender} : {
        message_type} : {number}\n'''  # you can edit
    with open(LOG_FILE, 'a') as f:
        f.write(log_entry)
    print(log_entry.strip())


def main():
    setup_logging()
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
    queue_1 = channel.queue_declare(queue='', exclusive=True).method.queue
    channel.queue_bind(
        exchange=EXCHANGE_NAME,
        queue=queue_1,
        routing_key='original'
    )
    queue_2 = channel.queue_declare(queue='', exclusive=True).method.queue
    channel.queue_bind(
        exchange=EXCHANGE_NAME,
        queue=queue_2,
        routing_key='squared'
    )
    queue_3 = channel.queue_declare(queue='', exclusive=True).method.queue
    channel.queue_bind(
        exchange=EXCHANGE_NAME,
        queue=queue_3,
        routing_key='cubed'
    )
    channel.basic_consume(
        queue=queue_1,
        on_message_callback=callback,
        auto_ack=True)
    channel.basic_consume(
        queue=queue_2,
        on_message_callback=callback,
        auto_ack=True)
    channel.basic_consume(
        queue=queue_3,
        on_message_callback=callback,
        auto_ack=True)
    try:
        channel.start_consuming()
    except KeyboardInterrupt:
        pass


if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        print(' [*] Logger stopped')
