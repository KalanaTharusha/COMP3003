package edu.curtin.saed.assignment1;

import javafx.scene.canvas.*;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A JavaFX GUI element that displays a grid on which you can draw images, text and lines.
 */
public class JFXArena extends Pane {
    // Represents an image to draw, retrieved as a project resource.
    private static final String ROBOT1_IMAGE_FILE = "1554047213.png";
    private static final String ROBOT2_IMAGE_FILE = "droid2.png";
    private static final String ROBOT3_IMAGE_FILE = "rg1024-robot-carrying-things-4.png";
    private static final String WALL_IMAGE_FILE = "181478.png";
    private static final String BROKEN_WALL_IMAGE_FILE = "181479.png";
    private static final String CITADEL_IMAGE_FILE = "rg1024-isometric-tower.png";
    private static final String CROSS_IMAGE_FILE = "cross.png";
    private Image robotIcon1, robotIcon2, robotIcon3, wallIcon, brokenWallIcon, citadelIcon, crossIcon;
    private int gridWidth = 9;
    private int gridHeight = 9;

    private double gridSquareSize; // Auto-calculated
    private Canvas canvas; // Used to provide a 'drawing surface'.

    private List<ArenaListener> listeners = null;
    private List<Robot> robots = new ArrayList<>(); // current robot list
    private List<Wall> walls = new ArrayList<>(); // current wall list
    public int wallCount = 0; // count of requested walls
    private BlockingQueue<Wall> wallBlockingQueue = new ArrayBlockingQueue<>(10); // queued walls
    public BlockingQueue<String> logBlockingQueue = new ArrayBlockingQueue<>(10); // queued log messages
    private List<Coords> spawnCoords = new ArrayList<>(4); // spawn coordinates
    private List<Coords> grid = new ArrayList<>(81); // grid coordinates

    private Random random = new Random();
    private final Object robotArrayLock = new Object(); // lock for accessing the robot list
    public int score = 0; // current score
    private ScheduledExecutorService scheduledExecutorService;
    private final AtomicBoolean gameOver = new AtomicBoolean(false);

    /**
     * Creates a new arena object, loading the robot image and initialising a drawing surface.
     */
    public JFXArena() {

        loadImages();

        for (double x = 0; x < 9; x++) {
            for (double y = 0; y < 9; y++) {
                Coords gridCoords = new Coords(x, y, false);
                grid.add(gridCoords);
            }
        }

        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

        canvas = new Canvas();
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        getChildren().add(canvas);
    }

    // Start the game
    public void start() {

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (gameOver.get()) {
                scheduledExecutorService.shutdown();
            }
            else {
                score = score + 10;
            }
        }, 0, 1, TimeUnit.SECONDS);

        spawn();
    }

    // Spawn new robots in every 1500ms
    private void spawn() {
        Thread spawnThread = new Thread(() -> {

            Coords topLeftCoords = new Coords(0.0, 0.0, false);
            Coords topRightCoords = new Coords(8.0, 0.0, false);
            Coords bottomLeftCoords = new Coords(0.0, 8.0, false);
            Coords bottomRightCoords = new Coords(8.0, 8.0, false);

            spawnCoords.add(topLeftCoords);
            spawnCoords.add(topRightCoords);
            spawnCoords.add(bottomLeftCoords);
            spawnCoords.add(bottomRightCoords);

            Image robotIcon;
            double spawnX;
            double spawnY;
            long delayedValue;
            int robotId = 1;

            ExecutorService pool = Executors.newFixedThreadPool(15);

            while (!gameOver.get()) {

                delayedValue = random.nextLong(2000 - 500) + 500;

                if (delayedValue < 1000) {
                    robotIcon = robotIcon3;
                } else if (delayedValue < 1500) {
                    robotIcon = robotIcon1;
                } else {
                    robotIcon = robotIcon2;
                }

                boolean available = false;
                Coords randomCoords;

                while (!available) {
                    if (gameOver.get()) {
                        break;
                    }
                    randomCoords = spawnCoords.get(random.nextInt(4));
                    spawnX = randomCoords.getX();
                    spawnY = randomCoords.getY();

                    if (!randomCoords.isOccupied()) {

                        randomCoords.setOccupied(true);
                        Robot robot = new Robot(robotId, spawnX, spawnY, delayedValue, robotIcon);

                        pool.execute(robotThread(robot, randomCoords));

                        robotId++;

                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        available = true;
                    }
                }

            }
            pool.shutdown();

        });
        spawnThread.start();
    }

    // Creates a robot thead and assign a robot to that
    @SuppressWarnings("PMD.SwitchStmtsShouldHaveDefault") // No need of default for this switch statement
    private Thread robotThread(Robot robot, Coords spawnCoords) {
        Thread robotThread = new Thread(() -> {

            if(!gameOver.get()){
                synchronized (robotArrayLock) {
                    robots.add(robot);
                    logBlockingQueue.add("Robot created");
                }

                while (robot.isAlive() && !gameOver.get()) {
                    try {
                        Thread.sleep(robot.getDelayedValue());
                        int func = random.nextInt(5);
                        switch (func) {
                            case 1 -> robot.moveUp(grid, spawnCoords);
                            case 2 -> robot.moveDown(grid, spawnCoords);
                            case 3 -> robot.moveRight(grid, spawnCoords);
                            case 4 -> robot.moveLeft(grid, spawnCoords);
                        }
                        checkCollisions();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

        });

        robotThread.setDaemon(true);
        return robotThread;
    }

    // Load image assets
    private void loadImages() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(ROBOT1_IMAGE_FILE)) {
            if (is == null) {
                throw new AssertionError("Cannot find image file " + ROBOT1_IMAGE_FILE);
            }
            robotIcon1 = new Image(is);
        } catch (IOException e) {
            throw new AssertionError("Cannot load image file " + ROBOT2_IMAGE_FILE, e);
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(ROBOT2_IMAGE_FILE)) {
            if (is == null) {
                throw new AssertionError("Cannot find image file " + ROBOT2_IMAGE_FILE);
            }
            robotIcon2 = new Image(is);
        } catch (IOException e) {
            throw new AssertionError("Cannot load image file " + ROBOT1_IMAGE_FILE, e);
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(ROBOT3_IMAGE_FILE)) {
            if (is == null) {
                throw new AssertionError("Cannot find image file " + ROBOT3_IMAGE_FILE);
            }
            robotIcon3 = new Image(is);
        } catch (IOException e) {
            throw new AssertionError("Cannot load image file " + ROBOT3_IMAGE_FILE, e);
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(WALL_IMAGE_FILE)) {
            if (is == null) {
                throw new AssertionError("Cannot find image file " + WALL_IMAGE_FILE);
            }
            wallIcon = new Image(is);
        } catch (IOException e) {
            throw new AssertionError("Cannot load image file " + WALL_IMAGE_FILE, e);
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(BROKEN_WALL_IMAGE_FILE)) {
            if (is == null) {
                throw new AssertionError("Cannot find image file " + BROKEN_WALL_IMAGE_FILE);
            }
            brokenWallIcon = new Image(is);
        } catch (IOException e) {
            throw new AssertionError("Cannot load image file " + BROKEN_WALL_IMAGE_FILE, e);
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CITADEL_IMAGE_FILE)) {
            if (is == null) {
                throw new AssertionError("Cannot find image file " + CITADEL_IMAGE_FILE);
            }
            citadelIcon = new Image(is);
        } catch (IOException e) {
            throw new AssertionError("Cannot load image file " + CITADEL_IMAGE_FILE, e);
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CROSS_IMAGE_FILE)) {
            if (is == null) {
                throw new AssertionError("Cannot find image file " + CROSS_IMAGE_FILE);
            }
            crossIcon = new Image(is);
        } catch (IOException e) {
            throw new AssertionError("Cannot load image file " + CROSS_IMAGE_FILE, e);
        }
    }

    /**
     * Moves a robot image to a new grid position. This is highly rudimentary, as you will need
     * many different robots in practice. This method currently just serves as a demonstration.
     */
    public void setRobotPosition() {
        requestLayout();
    }

    /**
     * Adds a callback for when the user clicks on a grid square within the arena. The callback
     * (of type ArenaListener) receives the grid (x,y) coordinates as parameters to the
     * 'squareClicked()' method.
     */
    public void addListener(ArenaListener newListener) {
        if (listeners == null) {
            listeners = new LinkedList<>();
            setOnMouseClicked(event ->
            {
                int gridX = (int) (event.getX() / gridSquareSize);
                int gridY = (int) (event.getY() / gridSquareSize);

                if (gridX < gridWidth && gridY < gridHeight) {
                    for (ArenaListener listener : listeners) {
                        listener.squareClicked(gridX, gridY);
                        if (wallCount < 10 && !(gridX == 4.0 && gridY == 4.0)) {
                            queueWall(gridX, gridY);
                            wallCount++;
                        }
                    }
                }
            });
        }
        listeners.add(newListener);
    }

    // Queue a wall to build
    private void queueWall(double x, double y) {
        try {
            Wall newWall = new Wall(1, x, y, 0, wallIcon);
            wallBlockingQueue.put(newWall);
            logBlockingQueue.add("Wall creating...");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Get a wall from wallBlockingQueue and start building
    public void buildWall() {
        Thread wallThread = new Thread(() -> {
            while (!gameOver.get()) {
                if (!wallBlockingQueue.isEmpty()) {
                    try {
                        Thread.sleep(2000);
                        walls.add(wallBlockingQueue.take());
                        logBlockingQueue.add("Wall created");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        wallThread.start();
    }

    // Check if there is any wall impacts
    private void checkCollisions() {
        synchronized (robotArrayLock) {
            List<Robot> destroyedRobots = new ArrayList<>();
            List<Wall> destroyedWalls = new ArrayList<>();

            Iterator<Robot> it = robots.iterator();
            while (it.hasNext()) {
                Robot robot = it.next();

                if (!gameOver.get() && robot.hit(4, 4)) {
                    citadelIcon = crossIcon;
                    logBlockingQueue.add("GAME OVER");
                    setGameOver();
                    break;
                }

                for (Wall wall : walls) {
                    if (robot.hit(wall.getX(), wall.getY())) {
                        robot.setAlive(false);
                        destroyedRobots.add(robot);
                        if (wall.getHits() == 1) {
                            wall.setHits(2);
                            destroyedWalls.add(wall);
                            wallCount--;
                            score = score + 100;
                            logBlockingQueue.add("Wall impacted");
                        } else {
                            wall.setHits(1);
                            wall.setIcon(brokenWallIcon);
                            score = score + 100;
                            logBlockingQueue.add("Wall impacted");
                        }
                        break;
                    }
                }
            }
            getChildren().removeAll(destroyedRobots);
            robots.removeAll(destroyedRobots);

            getChildren().removeAll(destroyedWalls);
            walls.removeAll(destroyedWalls);
        }
    }

    // Set game over
    public void setGameOver() {
        gameOver.set(true);
    }


    /**
     * This method is called in order to redraw the screen, either because the user is manipulating
     * the window, OR because you've called 'requestLayout()'.
     * <p>
     * You will need to modify the last part of this method; specifically the sequence of calls to
     * the other 'draw...()' methods. You shouldn't need to modify anything else about it.
     */
    @Override
    public void layoutChildren() {
        super.layoutChildren();
        GraphicsContext gfx = canvas.getGraphicsContext2D();
        gfx.clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

        // First, calculate how big each grid cell should be, in pixels. (We do need to do this
        // every time we repaint the arena, because the size can change.)
        gridSquareSize = Math.min(
                getWidth() / (double) gridWidth,
                getHeight() / (double) gridHeight);

        double arenaPixelWidth = gridWidth * gridSquareSize;
        double arenaPixelHeight = gridHeight * gridSquareSize;


        // Draw the arena grid lines. This may help for debugging purposes, and just generally
        // to see what's going on.
        gfx.setStroke(Color.DARKGREY);
        gfx.strokeRect(0.0, 0.0, arenaPixelWidth - 1.0, arenaPixelHeight - 1.0); // Outer edge

        for (int gridX = 1; gridX < gridWidth; gridX++) // Internal vertical grid lines
        {
            double x = (double) gridX * gridSquareSize;
            gfx.strokeLine(x, 0.0, x, arenaPixelHeight);
        }

        for (int gridY = 1; gridY < gridHeight; gridY++) // Internal horizontal grid lines
        {
            double y = (double) gridY * gridSquareSize;
            gfx.strokeLine(0.0, y, arenaPixelWidth, y);
        }

        // Invoke helper methods to draw things at the current location.
        // ** You will need to adapt this to the requirements of your application. **
        synchronized (robotArrayLock) {
            for (Robot robot : robots) {
                drawImage(gfx, robot.getIcon(), robot.getX(), robot.getY());
                drawLabel(gfx, String.valueOf(robot.getId()), robot.getX(), robot.getY());
            }
        }

        for (Wall wall : walls) {
            drawImage(gfx, wall.getIcon(), wall.getX(), wall.getY());
        }

        drawImage(gfx, citadelIcon, 4.0, 4.0);
    }


    /**
     * Draw an image in a specific grid location. *Only* call this from within layoutChildren().
     * <p>
     * Note that the grid location can be fractional, so that (for instance), you can draw an image
     * at location (3.5,4), and it will appear on the boundary between grid cells (3,4) and (4,4).
     * <p>
     * You shouldn't need to modify this method.
     */
    private void drawImage(GraphicsContext gfx, Image image, double gridX, double gridY) {
        // Get the pixel coordinates representing the centre of where the image is to be drawn. 
        double x = (gridX + 0.5) * gridSquareSize;
        double y = (gridY + 0.5) * gridSquareSize;

        // We also need to know how "big" to make the image. The image file has a natural width 
        // and height, but that's not necessarily the size we want to draw it on the screen. We 
        // do, however, want to preserve its aspect ratio.
        double fullSizePixelWidth = robotIcon1.getWidth();
        double fullSizePixelHeight = robotIcon1.getHeight();

        double displayedPixelWidth, displayedPixelHeight;
        if (fullSizePixelWidth > fullSizePixelHeight) {
            // Here, the image is wider than it is high, so we'll display it such that it's as 
            // wide as a full grid cell, and the height will be set to preserve the aspect 
            // ratio.
            displayedPixelWidth = gridSquareSize;
            displayedPixelHeight = gridSquareSize * fullSizePixelHeight / fullSizePixelWidth;
        } else {
            // Otherwise, it's the other way around -- full height, and width is set to 
            // preserve the aspect ratio.
            displayedPixelHeight = gridSquareSize;
            displayedPixelWidth = gridSquareSize * fullSizePixelWidth / fullSizePixelHeight;
        }

        // Actually put the image on the screen.
        gfx.drawImage(image,
                x - displayedPixelWidth / 2.0,  // Top-left pixel coordinates.
                y - displayedPixelHeight / 2.0,
                displayedPixelWidth,              // Size of displayed image.
                displayedPixelHeight);
    }


    /**
     * Displays a string of text underneath a specific grid location. *Only* call this from within
     * layoutChildren().
     * <p>
     * You shouldn't need to modify this method.
     */
    private void drawLabel(GraphicsContext gfx, String label, double gridX, double gridY) {
        gfx.setTextAlign(TextAlignment.CENTER);
        gfx.setTextBaseline(VPos.TOP);
        gfx.setStroke(Color.BLUE);
        gfx.strokeText(label, (gridX + 0.5) * gridSquareSize, (gridY + 1.0) * gridSquareSize);
    }

    /**
     * Draws a (slightly clipped) line between two grid coordinates.
     * <p>
     * You shouldn't need to modify this method.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod") // I don't use this provided method
    private void drawLine(GraphicsContext gfx, double gridX1, double gridY1,
                          double gridX2, double gridY2) {
        gfx.setStroke(Color.RED);

        // Recalculate the starting coordinate to be one unit closer to the destination, so that it
        // doesn't overlap with any image appearing in the starting grid cell.
        final double radius = 0.5;
        double angle = Math.atan2(gridY2 - gridY1, gridX2 - gridX1);
        double clippedGridX1 = gridX1 + Math.cos(angle) * radius;
        double clippedGridY1 = gridY1 + Math.sin(angle) * radius;

        gfx.strokeLine((clippedGridX1 + 0.5) * gridSquareSize,
                (clippedGridY1 + 0.5) * gridSquareSize,
                (gridX2 + 0.5) * gridSquareSize,
                (gridY2 + 0.5) * gridSquareSize);
    }
}
