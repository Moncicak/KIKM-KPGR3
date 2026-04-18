import java.util.ArrayList;
import java.util.List;

public class GridMeshFactory {

    /**
     * Vygeneruje vrcholy mřížky v rovině XY v rozsahu -1 až 1.
     */
    public static float[] generateVertices(int m, int n) {
        float[] vertices = new float[m * n * 3];
        int index = 0;

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                float x = (c / (float) (m - 1)) * 2.0f - 1.0f;
                float y = (r / (float) (n - 1)) * 2.0f - 1.0f;
                float z = 0.0f;

                vertices[index++] = x;
                vertices[index++] = y;
                vertices[index++] = z;
            }
        }
        return vertices;
    }

    /**
     * Varianta A: Seznam trojúhelníků (Triangle List)
     * Upraveno tak, aby diagonály tvořily jiný vzor než Strip.
     */
    public static int[] generateIndicesList(int m, int n) {
        int[] indices = new int[(m - 1) * (n - 1) * 6];
        int index = 0;

        for (int r = 0; r < n - 1; r++) {
            for (int c = 0; c < m - 1; c++) {
                int i0 = r * m + c;          // vlevo dole
                int i1 = r * m + (c + 1);    // vpravo dole
                int i2 = (r + 1) * m + c;    // vlevo nahoře
                int i3 = (r + 1) * m + (c + 1); // vpravo nahoře

                // První trojúhelník (i0, i1, i3)
                indices[index++] = i0;
                indices[index++] = i1;
                indices[index++] = i3;

                // Druhý trojúhelník (i0, i3, i2)
                indices[index++] = i0;
                indices[index++] = i3;
                indices[index++] = i2;
            }
        }
        return indices;
    }

    /**
     * Varianta B: Pás trojúhelníků (Triangle Strip)
     * Generuje cik-cak propojení řádků.
     */
    public static int[] generateIndicesStrip(int m, int n) {
        List<Integer> indices = new ArrayList<>();

        for (int r = 0; r < n - 1; r++) {
            for (int c = 0; c < m; c++) {
                indices.add(r * m + c);         // horní bod
                indices.add((r + 1) * m + c);   // spodní bod
            }

            // Degenerované trojúhelníky pro přesun na nový řádek
            if (r < n - 2) {
                indices.add((r + 1) * m + (m - 1)); // zopakujeme poslední
                indices.add((r + 1) * m);           // zopakujeme první nového řádku
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }
}