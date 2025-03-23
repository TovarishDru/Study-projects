#include <Python.h>
#include <stdlib.h>


int isNumerical(PyObject* input) {
    const char* str = PyUnicode_AsUTF8(input);
    for (size_t i = 0; i < strlen(str); i++) {
        if ('0' <= str[i] && str[i] <= '9') {
            return 1;
        }
    }
    return 0;
}


void concatStrings(PyObject* first, PyObject* second) {
    PyObject* res = PyUnicode_Concat(first, second);
    Py_DECREF(first);
    first = res;
}


PyObject* dumps(PyObject* self, PyObject* args){
    PyObject* dict = NULL;
    if (!PyArg_ParseTuple(args, "O", &dict)) {
        return NULL;
    }
    PyObject* key = NULL;
    PyObject* value = NULL;
    Py_ssize_t pos = 0;
    PyObject* json = PyUnicode_FromString("{");
    while (PyDict_Next(dict, &pos, &key, &value)) {
        PyObject* argKey = PyObject_Str(key);
        PyObject* argValue = PyObject_Str(value);
        PyObject* tmp = NULL;
        if (pos > 1) {
            PyObject* comma = PyUnicode_FromString(",");
            PyObject* res = PyUnicode_Concat(json, comma);
            Py_DECREF(json);
            json = res;
        }
        if (isNumerical(argValue)) {
            tmp = PyUnicode_FromFormat("\"%s\":%s", PyUnicode_AsUTF8(argKey), PyUnicode_AsUTF8(argValue));
        }
        else {
            tmp = PyUnicode_FromFormat("\"%s\":\"%s\"", PyUnicode_AsUTF8(argKey), PyUnicode_AsUTF8(argValue));
        }
        PyObject* res = PyUnicode_Concat(json, tmp);
        Py_DECREF(json);
        json = res;
        Py_DECREF(tmp);
		Py_DECREF(argKey);
		Py_DECREF(argValue);
    }
    PyObject* tmp = PyUnicode_FromString("}");
    PyObject* res = PyUnicode_Concat(json, tmp);
    Py_DECREF(json);
    json = res;
    Py_DECREF(tmp);
    return json;
}


int getKeyLen(const char* loadStr, int i) {
    int len = 0;
    while (loadStr[i] != '"' && loadStr[i] != ',' && loadStr[i] != '}') {
        i++;
        len++;
    }
    return len;
}


PyObject* getString(const char* loadStr, int* i) {
    (*i)++;
    int len = getKeyLen(loadStr, *i);
    char* str = (char*)malloc(sizeof(char) * (len + 1));
    int strIdx = 0;
    while (loadStr[*i] != '"') {
        str[strIdx++] = loadStr[*i];
        (*i)++;
    }
    str[strIdx] = '\0';
    PyObject* res = Py_BuildValue("s", str);
    free(str);
    return res;
}


PyObject* getInt(const char* loadStr, int* i) {
    int len = getKeyLen(loadStr, *i);
    char* num = (char*)malloc(sizeof(char) * (len + 1));
    int numIdx = 0;
    while (loadStr[*i] != ',' && loadStr[*i] != '}') {
        num[numIdx++] = loadStr[*i];
        (*i)++;
    }
    num[numIdx] = '\0';
    PyObject* res = Py_BuildValue("i", atoi(num));
    free(num);
    return res;
}


PyObject* getValue(const char* loadStr, int* i) {
    (*i)++;
    if (loadStr[*i] == '"') {
        return getString(loadStr, i);
    }
    else {
        return getInt(loadStr, i);
    }
}


PyObject* loads(PyObject* self, PyObject* args) {
    PyObject* dict = PyDict_New();
    char* loadStr = NULL;
    if (!PyArg_ParseTuple(args, "s", &loadStr)) {
        return NULL;
    }
    int i = 0;
    while (loadStr[i] != '}') {
        i++;
        if (loadStr[i] == '"') {
            i++;
            int len = getKeyLen(loadStr, i);
            char* key = (char*)malloc(sizeof(char) * (len + 1));
            for (size_t j = 0; j < len; j++) {
                key[j] = loadStr[i];
                i++;
            }
            key[len] = '\0';
            i++;
            PyObject* dictKey = Py_BuildValue("s", key);
            PyObject* dictValue = getValue(loadStr, &i);
            PyDict_SetItem(dict, dictKey, dictValue);
            free(key);
        }
    }
    return dict;
}


static PyMethodDef cjson_meths[] = {
    { "dumps", dumps, METH_VARARGS, "Converts a dictionary into a JSON" },
	{ "loads", loads, METH_VARARGS, "Converts a JSON into a dictionary" },
	{ NULL, NULL, 0, NULL }
};

static PyModuleDef cjson = {
    PyModuleDef_HEAD_INIT,
	"cjson",
	NULL,
	-1,
	cjson_meths
};

PyMODINIT_FUNC
PyInit_cjson(void)
{
	return PyModule_Create(&cjson);
}