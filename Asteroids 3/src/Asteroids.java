import javafx.scene.paint.Color;

public class Asteroids extends Game
{
    public Sprite spaceship;
    public Group rockGroup;
    public Texture rockTex;
    public Group laserGroup;
    public Texture laserTex;
    public Animation explosionAnim;
    public Sprite shields;
    public Sprite LoseMessage;
    public Sprite WinMessage;
    public Label timer;
    public Label ScoreValue;
    double time = 60.0;
    int score = 0;



    public void initialize()
    {
        setTitle("Asteroids");
        setWindowSize(800,600);

        Sprite background = new Sprite();
        Texture bgTex = new Texture("images/space.png");
        background.setTexture( bgTex );
        background.setPosition(400,300);
        group.add( background );

        spaceship = new Sprite();
        Texture ssTex = new Texture("images/spaceship.png");
        spaceship.setTexture(ssTex);
        spaceship.setPosition(400,300);
        spaceship.setPhysics( new Physics(200, 200, 20) );
        spaceship.addAction( Action.wrapToScreen(800,600) );
        group.add( spaceship );

        rockGroup = new Group();
        group.add( rockGroup );
        int rockCount = 8;
        rockTex = new Texture("images/asteroid.png");
        for (int i = 0; i < rockCount; i++)
        {
            Sprite rock = new Sprite();
            rock.setTexture( rockTex );
            rock.setSize(100,100);

            double angle = 2 * Math.PI * Math.random();
            double x = spaceship.position.x
                    + 250 * Math.cos(angle);
            double y = spaceship.position.y
                    + 250 * Math.sin(angle);
            rock.setPosition(x,y);

            rock.setPhysics( new Physics(0, 100, 0) );

            double moveSpeed = 30 * Math.random() + 90;
            rock.physics.setSpeed(moveSpeed);
            rock.physics.setMotionAngle(
                    Math.toDegrees(angle) );

            double rotateSpeed = 2 * Math.random() + 1;
            rock.addAction(
                    Action.forever(Action.rotateBy(360, rotateSpeed) )
            );

            rock.addAction( Action.wrapToScreen(800,600) );
            rockGroup.add(rock);
        }

        laserGroup = new Group();
        group.add( laserGroup );
        laserTex = new Texture("images/laser.png");

        explosionAnim = new Animation(
                "images/explosion.png", 6,6, 0.02, false);

        shields = new Sprite();
        Texture shieldTex = new Texture("images/shields.png");
        shields.setTexture( shieldTex );
        shields.setSize(120,120);
        group.add(shields);

        LoseMessage = new Sprite();
        Texture LoseMessageTex = new Texture("images/message-lose.png");
        LoseMessage.setTexture(LoseMessageTex);
        LoseMessage.setPosition(400,300);
        group.add(LoseMessage);
        LoseMessage.visible = false;

        WinMessage = new Sprite();
        Texture WinMessageTex = new Texture("images/message-win.png");
        WinMessage.setTexture(WinMessageTex);
        WinMessage.setPosition(400,300);
        group.add(WinMessage);
        WinMessage.visible = false;

        timer = new Label("Comic Sans MS Bold", 20);
        String text = "Time:" + Math.round(time);
        timer.setText(text);
        timer.fontColor = Color.LIGHTGREEN;
        timer.setBorder(2, Color.DARKGREEN);
        timer.setPosition(100, 50);
        timer.alignment = "CENTER";
        timer.visible = true;
        group.add(timer);

        ScoreValue = new Label("Comic Sans MS Bold", 25);
        String text1 = "Points: " + score;
        ScoreValue.setText(text1);
        ScoreValue.fontColor = Color.YELLOW;
        ScoreValue.setBorder(2, Color.YELLOW);
        ScoreValue.setPosition(690, 580);
        ScoreValue.alignment = "CENTER";
        ScoreValue.visible = true;
        group.add(ScoreValue);



    }

    public void update()
    {

        if (LoseMessage.visible | WinMessage.visible)
            return;

        time = time - 1.0/60.0;
        String text = "Time:" + Math.round(time);
        timer.setText(text);

        if (time < 0) {
        LoseMessage.visible = true;
        }


        shields.alignToSprite(spaceship);

        if ( input.isKeyPressed("LEFT") )
            spaceship.rotateBy(-3);

        if ( input.isKeyPressed("RIGHT") )
            spaceship.rotateBy(3);

        if ( input.isKeyPressed("UP") )
            spaceship.physics.accelerateAtAngle(spaceship.angle);

        if ( input.isKeyJustPressed("SPACE") && laserGroup.size() < 5)
        {

                Sprite laser = new Sprite();
                laser.setTexture(laserTex);
                laser.alignToSprite(spaceship);
                laser.setPhysics(new Physics(0, 400, 0));
                laser.physics.setSpeed(400);
                laser.physics.setMotionAngle(spaceship.angle);
                laser.addAction(Action.wrapToScreen(800, 600));
                laserGroup.add(laser);
                laser.addAction(Action.delayFadeRemove(1, 0.5));
        }

        for (Entity rockE : rockGroup.getList())
        {
            Sprite rock = (Sprite)rockE;

            if (shields.overlaps(rock) && shields.opacity > 0)
            {
                rock.removeSelf();
                shields.opacity -= 0.25;

                Sprite explosion = new Sprite();
                explosion.setAnimation(
                        explosionAnim.clone() );
                explosion.alignToSprite(rock);
                explosion.addAction( Action.animateThenRemove() );
                group.add( explosion );
            }

            // game over
            if (rock.overlaps(spaceship))
            {
                spaceship.removeSelf();
                Sprite explosion = new Sprite();
                explosion.setAnimation(
                        explosionAnim.clone() );
                explosion.alignToSprite(spaceship);
                explosion.addAction( Action.animateThenRemove() );
                group.add( explosion );
                LoseMessage.visible = true;
            }



            for (Entity laserE : laserGroup.getList())
            {
                Sprite laser = (Sprite)laserE;
                if (rock.overlaps(laser))
                {
                    score += 50;
                    String scorecurrent = "Points:" + score;
                    ScoreValue.setText(scorecurrent);

                    rockGroup.remove(rock);
                    laserGroup.remove(laser);
                    Sprite explosion = new Sprite();
                    explosion.setAnimation(
                            explosionAnim.clone() );
                    explosion.alignToSprite(rock);

                    explosion.addAction( Action.animateThenRemove() );


                    group.add( explosion );

                    // if rock is large (100x100),
                    //  split into two smaller rocks
                    if (rock.width == 100)
                    {
                        for (int i = 0; i < 2; i++)
                        {
                            Sprite rockSmall = new Sprite();
                            rockSmall.setTexture(rockTex);
                            rockSmall.setSize(50, 50);
                            rockSmall.alignToSprite(rock);
                            rockSmall.addAction(Action.wrapToScreen(800,600));
                            rockSmall.setPhysics(new Physics(0, 300, 0));
                            rockSmall.physics.setSpeed(
                                    2 * rock.physics.getSpeed());
                            rockSmall.physics.setMotionAngle(
                                    rock.physics.getMotionAngle() + 90*Math.random() - 45);

                            rockGroup.add(rockSmall);

                        }
                    }
                }
            }


            if (rockGroup.size() == 0)
            {
                score += time;
                score += shields.opacity * 200;
                String scorecurrent = "Points:" + score;
                ScoreValue.setText(scorecurrent);
                WinMessage.visible = true;
            }

        }
    }
}