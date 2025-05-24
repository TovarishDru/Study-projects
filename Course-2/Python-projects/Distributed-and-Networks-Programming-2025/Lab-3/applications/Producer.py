import pika
import sys
import time
import json
import random

import pika.exchange_type

from Globals import RMQ_HOST, RMQ_USER, RMQ_PASS, EXCHANGE_NAME


def publish(publisher_name: str, max_number: int, delay: float):
    connection = pika.BlockingConnection(pika.ConnectionParameters(
        host=RMQ_HOST,
        credentials=pika.PlainCredentials(RMQ_USER, RMQ_PASS)
    ))
    channel = connection.channel()
    for i in range(0, max_number):
        number = random.randint(0, 100)
        channel.exchange_declare(
            exchange=EXCHANGE_NAME,
            exchange_type=pika.exchange_type.ExchangeType.topic,
            durable=True
        )
        channel.basic_publish(
            exchange=EXCHANGE_NAME,
            routing_key='original',
            body=str(number)
        )
        time.sleep(delay)

    connection.close()


if __name__ == '__main__':
    if len(sys.argv) != 4:
        print("Usage: python Producer.py <publisher_name> <max_number> <delay_seconds>")
        sys.exit(1)

    try:
        publisher_name = str(sys.argv[1])
        max_number = int(sys.argv[2])
        delay = float(sys.argv[3])
    except ValueError:
        print("Error: <max_number> and <delay_seconds> arguments must be numbers")
        sys.exit(1)

    try:
        publish(publisher_name, max_number, delay)
    except KeyboardInterrupt:
        print('[x] Producer stopped')
        sys.exit(0)
