#include <string>
#include <stack>
#include <queue>
#include <algorithm>
#include <set>
#include <map>
#include <cmath>
#include <vector>
#include <memory>
#include <iostream>
#include <iomanip>
#include <stdio.h>
#include <cmath>
using namespace std;
using ll = long long;
using ull = unsigned long long;
using db = double;
using uint = unsigned int;


class Matrix {
protected:
    int n;
    int m;
    unique_ptr<unique_ptr<double[]>[]> matrix;
    friend ostream& operator<<(ostream& os, const Matrix& matrix) {
        string output = "";
        for (int i = 0; i < matrix.n; i++) {
            for (int j = 0; j < matrix.m; j++) {
                string num = to_string(round(matrix.matrix[i][j] * 10000) / 10000);
                for (int k = 0; k < num.size(); k++) {
                    if (num[k] == '.') {
                        output += num[k];
                        output += num[k + 1];
                        output += num[k + 2];
                        output += num[k + 3];
                        output += num[k + 4];
                        break;
                    }
                    output += num[k];
                }
                if (j < matrix.m - 1) {
                    output += " ";
                }
            }
            output += "\n";
        }
        return os << output;
    }
    friend istream& operator>>(istream& is, const Matrix& matrix) {
        for (int i = 0; i < matrix.n; i++) {
            for (int j = 0; j < matrix.m; j++) {
                is >> matrix.matrix[i][j];
            }
        }
        return is;
    }
public:
    Matrix operator+(const Matrix& matrix) {
        if (this->n == matrix.n and this->m == matrix.m) {
            Matrix res(this->n, this->m);
            for (int i = 0; i < this->n; i++) {
                for (int j = 0; j < this->m; j++) {
                    res.matrix[i][j] = this->matrix[i][j] + matrix.matrix[i][j];
                }
            }
            return res;
        }
        else {
            throw invalid_argument("Error: the dimensional problem occurred\n");
        }
    }
    Matrix* operator+(Matrix* matrix) {
        return new Matrix(*this + *matrix);
    }
    Matrix operator-(const Matrix& matrix) {
        if (this->n == matrix.n and this->m == matrix.m) {
            Matrix res(this->n, this->m);
            for (int i = 0; i < this->n; i++) {
                for (int j = 0; j < this->m; j++) {
                    res.matrix[i][j] = this->matrix[i][j] - matrix.matrix[i][j];
                }
            }
            return res;
        }
        else {
            throw invalid_argument("Error: the dimensional problem occurred\n");
        }
    }
    Matrix* operator-(Matrix* matrix) {
        return new Matrix(*this - *matrix);
    }
    Matrix operator*(const Matrix& matrix) {
        if (this->m == matrix.n) {
            Matrix res(this->n, matrix.m);
            for (int i = 0; i < this->n; i++) {
                for (int j = 0; j < matrix.m; j++) {
                    for (int k = 0; k < this->m; k++) {
                        res.matrix[i][j] += this->matrix[i][k] * matrix.matrix[k][j];
                    }
                }
            }
            return res;
        }
        else {
            throw invalid_argument("Error: the dimensional problem occurred\n");
        }
    }
    Matrix* operator*(Matrix* matrix) {
        return new Matrix(*this * *matrix);
    }
    Matrix operator=(const Matrix& matrix) {
        this->n = matrix.n;
        this->m = matrix.m;
        this->matrix.release();
        this->matrix = unique_ptr<unique_ptr<double[]>[]>(new unique_ptr<double[]>[this->n]);
        for (int i = 0; i < matrix.n; i++) {
            this->matrix[i] = unique_ptr<double[]>(new double[this->m]);
            for (int j = 0; j < matrix.m; j++) {
                this->matrix[i][j] = matrix.matrix[i][j];
            }
        }
        return *this;
    }
    Matrix* operator=(Matrix* matrix) {
        *this = *matrix;
        return this;
    }
    Matrix transpose() {
        Matrix res(this->m, this->n);
        for (int i = 0; i < this->n; i++) {
            for (int j = 0; j < this->m; j++) {
                res.matrix[j][i] = this->matrix[i][j];
            }
        }
        return res;
    }
    int getN() const {
        return this->n;
    }
    int getM() const {
        return this->m;
    }
    double getElem(int i, int j) const {
        if (i >= this->n or j >= this->m) {
            throw invalid_argument("Error: the dimensional problem occurred\n");
        }
        return this->matrix[i][j];
    }
    Matrix() = delete;
    Matrix(int n, int m) {
        this->n = n;
        this->m = m;
        matrix = unique_ptr<unique_ptr<double[]>[]>(new unique_ptr<double[]>[n]);
        for (int i = 0; i < n; i++) {
            matrix[i] = unique_ptr<double[]>(new double[m]);
            for (int j = 0; j < m; j++) {
                matrix[i][j] = 0;
            }
        }
    }
    Matrix(const Matrix& matrix) {
        this->n = matrix.n;
        this->m = matrix.m;
        this->matrix = unique_ptr<unique_ptr<double[]>[]>(new unique_ptr<double[]>[this->n]);
        for (int i = 0; i < matrix.n; i++) {
            this->matrix[i] = unique_ptr<double[]>(new double[this->m]);
            for (int j = 0; j < matrix.m; j++) {
                this->matrix[i][j] = matrix.matrix[i][j];
            }
        }
    }
    Matrix(const vector<vector<double>>& input) {
        this->n = input.size();
        this->m = input[0].size();
        this->matrix = unique_ptr<unique_ptr<double[]>[]>(new unique_ptr<double[]>[this->n]);
        for (int i = 0; i < this->n; i++) {
            this->matrix[i] = unique_ptr<double[]>(new double[this->m]);
            for (int j = 0; j < this->m; j++) {
                this->matrix[i][j] = input[i][j];
            }
        }
    }
};


class ColumnVector : public Matrix {
public:
    double norm() {
        double res = 0;
        for (int i = 0; i < this->n; i++) {
            res += pow(this->matrix[i][0], 2);
        }
        return sqrt(res);
    }
    ColumnVector() = delete;
    ColumnVector(int n) : Matrix(n, 1) { }
    ColumnVector(const Matrix& matrix) : Matrix(matrix.getN(), 1) {
        if (matrix.getM() != 1) {
            throw invalid_argument("Error: the dimensional problem occurred\n");
        }
        for (int i = 0; i < matrix.getN(); i++) {
            this->matrix[i][0] = matrix.getElem(i, 0);
        }
    }
    ColumnVector(const vector<double>& input) : Matrix(input.size(), 1) {
        for (int i = 0; i < input.size(); i++) {
            this->matrix[i][0] = input[i];
        }
    }
};


class SquareMatrix : public Matrix {
public:
    Matrix operator+(const Matrix& matrix) {
        Matrix first(*this);
        return first + matrix;
    }
    Matrix operator-(const Matrix& matrix) {
        Matrix first(*this);
        return first - matrix;
    }
    Matrix operator*(const Matrix& matrix) {
        Matrix first(*this);
        return first * matrix;
    }
    Matrix* operator+(Matrix* matrix) {
        return (SquareMatrix*)(*(Matrix*)this + matrix);
    }
    Matrix* operator-(Matrix* matrix) {
        return (SquareMatrix*)(*(Matrix*)this - matrix);
    }
    Matrix* operator*(Matrix* matrix) {
        return (SquareMatrix*)(*(Matrix*)this * matrix);
    }
    Matrix operator=(const Matrix& matrix) {
        if (matrix.getN() != matrix.getM()) {
            throw invalid_argument("Error: the dimensional problem occurred\n");
        }
        this->n = matrix.getN();
        this->m = matrix.getM();
        this->matrix.release();
        this->matrix = unique_ptr<unique_ptr<double[]>[]>(new unique_ptr<double[]>[this->n]);
        for (int i = 0; i < matrix.getN(); i++) {
            this->matrix[i] = unique_ptr<double[]>(new double[this->m]);
            for (int j = 0; j < matrix.getM(); j++) {
                this->matrix[i][j] = matrix.getElem(i, j);
            }
        }
        return *this;
    }
    SquareMatrix() = delete;
    SquareMatrix(int n) : Matrix(n, n) {};
    SquareMatrix(const Matrix& matrix) : Matrix(matrix) {
        if (matrix.getN() != matrix.getM()) {
            throw invalid_argument("Error: the dimensional problem occurred\n");
        }
    }
};


class IdentityMatrix : public SquareMatrix {
public:
    IdentityMatrix() = delete;
    IdentityMatrix(int n) : SquareMatrix(n) {
        for (int i = 0; i < n; i++) {
            this->matrix[i][i] = 1;
        }
    }
};


class PermutationMatrix : public SquareMatrix {
public:
    PermutationMatrix() = delete;
    PermutationMatrix(const Matrix& matrix, int v, int u) : SquareMatrix(matrix.getN()) {
        if (v > this->n or u > this->m) {
            throw invalid_argument("Error: the dimensional problem occurred\n");
        }
        for (int i = 0; i < this->n; i++) {
            if (i == v - 1) {
                this->matrix[i][u - 1] = 1;
            }
            else if (i == u - 1) {
                this->matrix[i][v - 1] = 1;
            }
            else {
                this->matrix[i][i] = 1;
            }
        }
    }
};


class EliminationMatrix : public SquareMatrix {
public:
    EliminationMatrix() = delete;
    EliminationMatrix(const Matrix& matrix, int e_v, int e_u, int p_v, int p_u) : SquareMatrix(matrix.getN()) {
        if (e_v > this->n or e_u > matrix.getM() or p_v > this->n or p_u > matrix.getM()) {
            throw invalid_argument("Error: the dimensional problem occurred\n");
        }
        for (int i = 0; i < this->n; i++) {
            this->matrix[i][i] = 1;
            if (i == e_v - 1) {
                this->matrix[i][p_v - 1] = -matrix.getElem(i, e_u - 1) / matrix.getElem(p_v - 1, p_u - 1);
            }
        }
    }
};


class NormalizationMatrix : public SquareMatrix {
public:
    NormalizationMatrix() = delete;
    NormalizationMatrix(const Matrix& matrix, int row) : SquareMatrix(matrix.getN()) {
        for (int i = 0; i < n; i++) {
            this->matrix[i][i] = 1;
            if (row - 1 == i) {
                this->matrix[i][i] = 1 / matrix.getElem(i, i);
            }
        }
    }
};


class ConjugateMatrices {
protected:
    Matrix* m1;
    Matrix* m2;
public:
    friend ostream& operator<<(ostream& os, const ConjugateMatrices& matrices) {
        string output = "";
        for (int i = 0; i < matrices.m1->getN(); i++) {
            for (int j = 0; j < matrices.m1->getM(); j++) {
                string num = to_string(round(matrices.m1->getElem(i, j) * 100) / 100);
                for (int k = 0; k < num.size(); k++) {
                    if (num[k] == '.') {
                        output += num[k];
                        output += num[k + 1];
                        output += num[k + 2];
                        break;
                    }
                    output += num[k];
                }
                output += " ";
            }
            for (int j = 0; j < matrices.m2->getM(); j++) {
                string num = to_string(round(matrices.m2->getElem(i, j) * 100) / 100);
                for (int k = 0; k < num.size(); k++) {
                    if (num[k] == '.') {
                        output += num[k];
                        output += num[k + 1];
                        output += num[k + 2];
                        break;
                    }
                    output += num[k];
                }
                if (j < matrices.m2->getM() - 1) {
                    output += " ";
                }
            }
            output += "\n";
        }
        return os << output;
    }
    ConjugateMatrices() = delete;
    ConjugateMatrices(Matrix* m1, Matrix* m2) {
        if (m1->getN() != m2->getN()) {
            throw invalid_argument("Error: the dimensional problem occurred\n");
        }
        this->m1 = m1;
        this->m2 = m2;
    }
};


class JacobiAlphaMatrix : public Matrix {
public:
    JacobiAlphaMatrix(const Matrix& matrix) : Matrix(matrix.getN(), matrix.getM()) {
        for (int i = 0; i < this->n; i++) {
            double pivot = abs(matrix.getElem(i, i));
            double sum = 0;
            for (int j = 0; j < this->m; j++) {
                if (i != j) {
                    this->matrix[i][j] = -matrix.getElem(i, j) / matrix.getElem(i, i);
                    sum += abs(matrix.getElem(i, j));
                }
                else {
                    this->matrix[i][j] = 0;
                }
            }
            if (sum >= pivot) {
                throw invalid_argument("The method is not applicable\n");
            }
        }
    }
};


class JacobiBetaVector : public Matrix {
public:
    JacobiBetaVector(const Matrix& matrix, const ColumnVector& vector) : Matrix(vector.getN(), 1) {
        for (int i = 0; i < this->n; i++) {
            this->matrix[i][0] = vector.getElem(i, 0) / matrix.getElem(i, i);
        }
    }
};


void makeUpperTrinagular(Matrix& matrix, Matrix& e, int& steps, bool out) {
    int pivots = 0;
    for (int j = 0; j < matrix.getM(); j++) {
        if (pivots >= matrix.getN() - 1) {
            break;
        }
        double maxVal = abs(matrix.getElem(pivots, j));
        int row = pivots;
        for (int i = pivots + 1; i < matrix.getN(); i++) {
            if (abs(matrix.getElem(i, j)) > maxVal) {
                maxVal = abs(matrix.getElem(i, j));
                row = i;
            }
        }
        if (maxVal == 0) {
            continue;
        }
        if (row != pivots) {
            PermutationMatrix tmpMatrix(matrix, pivots + 1, row + 1);
            matrix = tmpMatrix * matrix;
            e = tmpMatrix * e;
        }
        for (int i = pivots + 1; i < matrix.getN(); i++) {
            if (matrix.getElem(i, j) != 0) {
                EliminationMatrix tmpMatrix(matrix, i + 1, j + 1, pivots + 1, j + 1);
                matrix = tmpMatrix * matrix;
                e = tmpMatrix * e;
            }
        }
        pivots++;
    }
}


void makeDownTrinagular(Matrix& matrix, Matrix& e, int& steps, bool out, bool second) {
    int pivots = 0;
    for (int j = matrix.getM() - 1; j > 0; j--) {
        if (pivots >= matrix.getN() - 1) {
            break;
        }
        if (!second) {
            double maxVal = abs(matrix.getElem(matrix.getN() - pivots - 1, j));
            int row = matrix.getN() - pivots - 1;
            for (int i = matrix.getN() - pivots - 2; i >= 0; i--) {
                if (abs(matrix.getElem(i, j)) > maxVal) {
                    maxVal = abs(matrix.getElem(i, j));
                    row = i;
                }
            }
            if (maxVal == 0) {
                continue;
            }
            if (row != matrix.getN() - pivots - 1) {
                PermutationMatrix tmpMatrix(matrix, matrix.getN() - pivots, row + 1);
                matrix = tmpMatrix * matrix;
                e = tmpMatrix * e;
            }
        }
        for (int i = matrix.getN() - pivots - 2; i >= 0; i--) {
            if (matrix.getElem(i, j) != 0) {
                EliminationMatrix tmpMatrix(matrix, i + 1, j + 1, matrix.getN() - pivots, j + 1);
                matrix = tmpMatrix * matrix;
                e = tmpMatrix * e;
            }
        }
        pivots++;
    }
}


bool isSingular(SquareMatrix matrix) {
    int c = 0;
    SquareMatrix tmp(IdentityMatrix(matrix.getN()));
    makeUpperTrinagular(matrix, tmp, c, false);
    for (int i = 0; i < matrix.getN(); i++) {
        if (matrix.getElem(i, i) == 0) {
            return true;
        }
    }
    return false;
}


void diagonalNormalization(SquareMatrix& matrix, Matrix& e) {
    for (int i = 0; i < matrix.getN(); i++) {
        if (matrix.getElem(i, i) == 0) {
            continue;
        }
        NormalizationMatrix tmpMatrix(matrix, i + 1);
        matrix = tmpMatrix * matrix;
        e = tmpMatrix * e;
    }
}


void eliminate(SquareMatrix matrix, ColumnVector vector) {
    int c = 0;
    if (isSingular(matrix)) {
        throw invalid_argument("Error: matrix A is singular\n");
    }
    makeUpperTrinagular(matrix, vector, c, true);
    makeDownTrinagular(matrix, vector, c, true, true);
    diagonalNormalization(matrix, vector);
}


SquareMatrix findInverse(SquareMatrix matrix) {
    int c = 0;
    if (isSingular(matrix)) {
        throw invalid_argument("Error: matrix A is singular\n");
    }
    SquareMatrix e(IdentityMatrix(matrix.getN()));
    ConjugateMatrices matrices(&matrix, &e);
    makeUpperTrinagular(matrix, e, c, true);
    makeDownTrinagular(matrix, e, c, true, true);
    diagonalNormalization(matrix, e);
    return e;
}


SquareMatrix getLowerPart(const SquareMatrix& matrix) {
    vector<vector<double>> lower(matrix.getN(), vector<double>(matrix.getN(), 0));
    for (int i = 0; i < matrix.getN(); i++) {
        for (int j = 0; j < i; j++) {
            lower[i][j] = matrix.getElem(i, j);
        }
    }
    return SquareMatrix(lower);
}


string print(const double& db) {
    string num = to_string(round(db * 100) / 100);
    string output = "";
    for (int k = 0; k < num.size(); k++) {
        if (num[k] == '.') {
            output += num[k];
            output += num[k + 1];
            output += num[k + 2];
            break;
        }
        output += num[k];
    }
    return output;
}


int main() {
    try {
        double v0;
        double k0;
        double a1, b1, a2, b2;
        int T;
        int n;
        cin >> v0 >> k0 >> a1 >> b1 >> a2 >> b2 >> T >> n;
        double step = 1.0 * T / n;
        vector<double> moments(n, 0);
        vector<double> victims(n, 0);
        vector<double> killers(n, 0);
        for (int i = 0; i <= n; i++) {
            if (i > 0) {
                moments[i] = moments[i - 1] + step;
            }
            double t = moments[i];
            victims[i] = (v0 - a2 / b2) * cos(sqrt(a1 * a2) * t ) - (k0 - a1 / b1) * (sqrt(a2) * b1 / (b2 * sqrt(a1))) * sin(sqrt(a1 * a2) * t) + a2 / b2;
            killers[i] = (v0 - a2 / b2) * (sqrt(a1) * b2 / (b1 * sqrt(a2))) * sin(sqrt(a1 * a2) * t) + (k0 - a1 / b1) * cos(sqrt(a1 * a2) * t) + a1 / b1;
        }
        cout << "t:\n";
        for (int i = 0; i <= n; i++) {
            cout << print(moments[i]) << " ";
        }
        cout << "\nv:\n";
        for (int i = 0; i <= n; i++) {
            cout << print(victims[i]) << " ";
        }
        cout << "\nk:\n";
        for (int i = 0; i <= n; i++) {
            cout << print(killers[i]) << " ";
        }
        cout << "\n";
    }
    catch (const exception& ex) {
        cout << ex.what() << "\n";
    }
    return 0;
}