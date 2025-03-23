all: build

build:
	python3 setup.py build

install:
	sudo python3 setup.py install

test: build install
	python3 test/test.py

clean:
	rm -rf build/