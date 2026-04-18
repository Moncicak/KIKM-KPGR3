import java.util.ArrayList;
import java.util.List;

import lwjglutils.OGLBuffers;

public final class GridMeshFactory {

    private GridMeshFactory() {
    }

    public static OGLBuffers createGrid(int rows, int cols, GridTopology topology) {
        if (rows < 2 || cols < 2) {
            throw new IllegalArgumentException("Grid resolution must be at least 2x2");
        }

        float[] vertices = new float[rows * cols * 5];
        int vertexOffset = 0;
        for (int row = 0; row < rows; row++) {
            float v = row / (float) (rows - 1);
            float y = -1.0f + 2.0f * v;
            for (int col = 0; col < cols; col++) {
                float u = col / (float) (cols - 1);
                float x = -1.0f + 2.0f * u;

                vertices[vertexOffset++] = x;
                vertices[vertexOffset++] = y;
                vertices[vertexOffset++] = 0.0f;
                vertices[vertexOffset++] = u;
                vertices[vertexOffset++] = v;
            }
        }

        int[] indices = topology == GridTopology.TRIANGLE_LIST
                ? createTriangleListIndices(rows, cols)
                : createTriangleStripIndices(rows, cols);

        return new OGLBuffers(vertices, new OGLBuffers.Attrib[]{
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inTexCoord", 2)
        }, indices);
    }

    private static int[] createTriangleListIndices(int rows, int cols) {
        int[] indices = new int[(rows - 1) * (cols - 1) * 6];
        int offset = 0;
        for (int row = 0; row < rows - 1; row++) {
            for (int col = 0; col < cols - 1; col++) {
                int topLeft = row * cols + col;
                int topRight = topLeft + 1;
                int bottomLeft = topLeft + cols;
                int bottomRight = bottomLeft + 1;

                indices[offset++] = topLeft;
                indices[offset++] = bottomLeft;
                indices[offset++] = topRight;

                indices[offset++] = topRight;
                indices[offset++] = bottomLeft;
                indices[offset++] = bottomRight;
            }
        }
        return indices;
    }

    private static int[] createTriangleStripIndices(int rows, int cols) {
        List<Integer> indices = new ArrayList<>();
        for (int row = 0; row < rows - 1; row++) {
            if (row > 0) {
                indices.add(row * cols);
            }
            for (int col = 0; col < cols; col++) {
                indices.add(row * cols + col);
                indices.add((row + 1) * cols + col);
            }
            if (row < rows - 2) {
                indices.add((row + 1) * cols + (cols - 1));
            }
        }
        int[] result = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            result[i] = indices.get(i);
        }
        return result;
    }
}
