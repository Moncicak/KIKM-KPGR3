import lwjglutils.OGLBuffers;
import lwjglutils.OGLModelOBJ;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import transforms.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer extends AbstractRenderer {

	private int paramProgram;
	private int modelProgram;
	private int renderMode = GL_FILL;
	private OGLBuffers buffersList;
	private OGLBuffers buffersStrip;
	private OGLModelOBJ staticBody;
	private GridTopology topology = GridTopology.TRIANGLES;

	private int functionMode = 0;
	private int debugMode = 0;
	private float rotX = 0, rotY = 0;

	// --- KAMERA A OVLÁDÁNÍ ---
	private Camera camera = new Camera();
	private Mat4 projection;
	private boolean[] keys = new boolean[1024];
	private boolean mousePressed = false;
	private double oldX, oldY;
	private boolean isPerspective = true;

	// --- OSVĚTLENÍ (Reflektor + Útlum + Znázornění) ---
	private Vec3D lightPos = new Vec3D(1.5, 1.5, 2.0);
	private Vec3D lightDir = new Vec3D(-1.0, -1.0, -1.0); // Směr, kam reflektor svítí

	@Override
	public void init() {
		super.init();
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		int m = 50;
		int n = 50;
		float[] vertices = GridMeshFactory.generateVertices(m, n);
		OGLBuffers.Attrib[] attributes = { new OGLBuffers.Attrib("inPosition", 3) };

		buffersList = new OGLBuffers(vertices, attributes, GridMeshFactory.generateIndicesList(m, n));
		buffersStrip = new OGLBuffers(vertices, attributes, GridMeshFactory.generateIndicesStrip(m, n));

		try {
			staticBody = new OGLModelOBJ("/obj/StaticBody.obj");
		} catch (Exception e) {
			System.err.println("Nepodařilo se načíst model StaticBody.obj!");
		}

		paramProgram = ShaderUtils.loadProgram("/shaders/start");
		modelProgram = ShaderUtils.loadProgram("/shaders/static");

		// Výchozí nastavení kamery
		camera = camera.withPosition(new Vec3D(0, -3, 1))
				.withAzimuth(Math.PI / 2)
				.withZenith(-Math.PI / 8);
	}

	@Override
	public void display() {
		updateCamera();
		pass++;

		glViewport(0, 0, width, height);
		glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// --- NASTAVENÍ PROJEKCE ---
		double aspect = height / (double) (width > 0 ? width : 1);
		if (isPerspective) {
			projection = new Mat4PerspRH(Math.PI / 3, aspect, 0.1, 50.0);
		} else {
			projection = new Mat4OrthoRH(2.5 / aspect, 2.5, 0.1, 50.0);
		}

		// --- KRESLENÍ ---
		drawProceduralBody();
		drawStaticBody();
		drawLightSource(); // Znázornění zdroje světla v prostoru

		// --- TEXTOVÝ VÝSTUP ---
		textRenderer.clear();
		textRenderer.addStr2D(3, 20, "WSAD: pohyb kamery | Myš: rozhlížení | Šipky: rotace modelu");
		textRenderer.addStr2D(3, 40, "IJKU: pohyb světla | O: Přepnout projekci (" + (isPerspective ? "Persp" : "Orto") + ")");
		textRenderer.addStr2D(3, 60, "M: Režim osvětlení (0: vše, 1: amb, 2: diff, 3: spec)");
		textRenderer.draw();
	}

	private void updateCamera() {
		double speed = 0.05;
		if (keys[GLFW_KEY_W]) camera = camera.forward(speed);
		if (keys[GLFW_KEY_S]) camera = camera.backward(speed);
		if (keys[GLFW_KEY_A]) camera = camera.left(speed);
		if (keys[GLFW_KEY_D]) camera = camera.right(speed);
	}

	private void drawProceduralBody() {
		glUseProgram(paramProgram);

		Mat4 modelMatrix = new Mat4Transl(-0.6, 0, 0).mul(new Mat4RotXYZ(rotX, rotY, 0));
		Mat4 mvp = modelMatrix.mul(camera.getViewMatrix()).mul(projection);

		// Matice
		glUniformMatrix4fv(glGetUniformLocation(paramProgram, "modelViewProjection"), false, ToFloatArray.convert(mvp));
		glUniformMatrix4fv(glGetUniformLocation(paramProgram, "modelMatrix"), false, ToFloatArray.convert(modelMatrix));

		// Reflektor, útlum a Blinn-Phong data
		glUniform3f(glGetUniformLocation(paramProgram, "lightPos"), (float)lightPos.getX(), (float)lightPos.getY(), (float)lightPos.getZ());
		glUniform3f(glGetUniformLocation(paramProgram, "lightDirUniform"), (float)lightDir.getX(), (float)lightDir.getY(), (float)lightDir.getZ());
		glUniform3f(glGetUniformLocation(paramProgram, "viewPos"), (float)camera.getPosition().getX(), (float)camera.getPosition().getY(), (float)camera.getPosition().getZ());

		glUniform1f(glGetUniformLocation(paramProgram, "time"), pass / 50.0f);
		glUniform1i(glGetUniformLocation(paramProgram, "mode"), functionMode);
		glUniform1i(glGetUniformLocation(paramProgram, "debugMode"), debugMode);

		glPolygonMode(GL_FRONT_AND_BACK, renderMode);
		drawGeometry();
	}

	private void drawStaticBody() {
		if (staticBody == null || staticBody.getBuffers() == null) return;
		glUseProgram(modelProgram);

		Mat4 model = new Mat4Transl(0.6, 0, 0).mul(new Mat4Scale(0.35));
		Mat4 mvp = model.mul(camera.getViewMatrix()).mul(projection);

		glUniformMatrix4fv(glGetUniformLocation(modelProgram, "modelViewProjection"), false, ToFloatArray.convert(mvp));
		glUniformMatrix4fv(glGetUniformLocation(modelProgram, "modelMatrix"), false, ToFloatArray.convert(model));

		// Světlo pro statické těleso (aby taky bylo stínované)
		glUniform3f(glGetUniformLocation(modelProgram, "lightPos"), (float)lightPos.getX(), (float)lightPos.getY(), (float)lightPos.getZ());
		glUniform3f(glGetUniformLocation(modelProgram, "viewPos"), (float)camera.getPosition().getX(), (float)camera.getPosition().getY(), (float)camera.getPosition().getZ());

		glUniform1i(glGetUniformLocation(modelProgram, "debugMode"), debugMode);
		glUniform3f(glGetUniformLocation(modelProgram, "uColor"), 0.85f, 0.75f, 0.25f);

		staticBody.getBuffers().draw(staticBody.getTopology(), modelProgram);
	}

	private void drawLightSource() {
		if (staticBody == null || staticBody.getBuffers() == null) return;
		glUseProgram(modelProgram);

		// Malá bílá kostička znázorňující zdroj světla
		Mat4 model = new Mat4Transl(lightPos).mul(new Mat4Scale(0.05));
		Mat4 mvp = model.mul(camera.getViewMatrix()).mul(projection);

		glUniformMatrix4fv(glGetUniformLocation(modelProgram, "modelViewProjection"), false, ToFloatArray.convert(mvp));
		glUniform3f(glGetUniformLocation(modelProgram, "uColor"), 1.0f, 1.0f, 1.0f); // Bílá

		staticBody.getBuffers().draw(staticBody.getTopology(), modelProgram);
	}

	private void drawGeometry() {
		if (topology == GridTopology.TRIANGLES) {
			if (buffersList != null) buffersList.draw(GL_TRIANGLES, paramProgram);
		} else {
			if (buffersStrip != null) buffersStrip.draw(GL_TRIANGLE_STRIP, paramProgram);
		}
	}

	@Override
	public GLFWKeyCallback getKeyCallback() {
		return new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key < 0 || key >= 1024) return;
				if (action == GLFW_PRESS) keys[key] = true;
				else if (action == GLFW_RELEASE) keys[key] = false;

				if (action == GLFW_PRESS || action == GLFW_REPEAT) {
					switch (key) {
						case GLFW_KEY_P -> renderMode = GL_POINT;
						case GLFW_KEY_L -> renderMode = GL_LINE;
						case GLFW_KEY_F -> renderMode = GL_FILL;
						case GLFW_KEY_1 -> functionMode = 0;
						case GLFW_KEY_2 -> functionMode = 1;
						case GLFW_KEY_3 -> functionMode = 2;
						case GLFW_KEY_4 -> functionMode = 3;
						case GLFW_KEY_5 -> functionMode = 4;
						case GLFW_KEY_6 -> functionMode = 5;
						case GLFW_KEY_M -> debugMode = (debugMode + 1) % 4;
						case GLFW_KEY_O -> isPerspective = !isPerspective;

						case GLFW_KEY_UP -> rotX += 0.1f;
						case GLFW_KEY_DOWN -> rotX -= 0.1f;
						case GLFW_KEY_LEFT -> rotY -= 0.1f;
						case GLFW_KEY_RIGHT -> rotY += 0.1f;

						// Ovládání pozice světla
						case GLFW_KEY_I -> lightPos = lightPos.add(new Vec3D(0, 0, 0.2));
						case GLFW_KEY_K -> lightPos = lightPos.add(new Vec3D(0, 0, -0.2));
						case GLFW_KEY_J -> lightPos = lightPos.add(new Vec3D(-0.2, 0, 0));
						case GLFW_KEY_U -> lightPos = lightPos.add(new Vec3D(0.2, 0, 0));

						case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true);
					}
				}
			}
		};
	}

	@Override
	public GLFWMouseButtonCallback getMouseCallback() {
		return new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (button == GLFW_MOUSE_BUTTON_1) mousePressed = (action == GLFW_PRESS);
			}
		};
	}

	@Override
	public GLFWCursorPosCallback getCursorCallback() {
		return new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				if (mousePressed) {
					camera = camera.addAzimuth((oldX - xpos) * 0.005);
					camera = camera.addZenith((oldY - ypos) * 0.005);
				}
				oldX = xpos; oldY = ypos;
			}
		};
	}
}