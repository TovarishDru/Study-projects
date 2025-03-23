from setuptools import setup, Extension


cjson_module = Extension('cjson',
                         sources=['src/cjson.c'])


setup(name='cjson',
      ext_modules=[cjson_module])


if __name__ == "__main__":
    setup()