// Based on work preseted here presented http://www.graficaobscura.com/matrix/index.html
// By Paul Haeberli - 1993
#ifndef MATRIX_H
#define MATRIX_H

#include <math.h>
#include "bitmap.h"

void applyMatrix(Bitmap* bitmap, float matrix[4][4]);

void applyMatrixToPixel(unsigned char* red, unsigned char* green, unsigned char* blue, float matrix[4][4]);

void saturateMatrix(float matrix[4][4], float* saturation);

void multiplyMatricies(float a[4][4], float b[4][4], float c[4][4]);

void identMatrix(float *matrix);

#endif



