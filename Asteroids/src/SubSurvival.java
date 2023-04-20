import javafx.scene.paint.Color;
//Import the javafx for later use in our labels

public class SubSurvival extends Game
{
   //We set every sprite, group, texture, animation, and label we are going to be using.
    public Sprite water;
    public Sprite core;
    public Sprite player;
    public Group enemyGroup;
    public Texture enemyTex;
    public Label healthLabel;
    public Texture bulletTex;
    public double enemySpeed;
    public double enemyTimer;
    public Group bulletGroup;
    public int score;
    public int health;
    public Label scoreLabel;
    public Sprite LoseMessage;
    public Animation explosionAnim;


    public void initialize()
    {
        setTitle("Sub Survival");
        setWindowSize(640,480);//give the window size
        health = 100;//core health starts at a 100

        //set the background
        water = new Sprite();
        Texture waterTex = new Texture("/water.png");
        water.setTexture( waterTex );
        water.setPosition(320,240);
        group.add(water);

        player = new Sprite();
        Texture playerTex = new Texture("/player.png");
        player.setTexture(playerTex);
        player.setPosition(150,250);
        player.setPhysics( new Physics(550, 300, 10) );
        player.addAction( Action.boundToScreen(640,480) );
        group.add( player );

        core = new Sprite();
        Texture coreTex = new Texture("/core.png");
        core.setTexture( coreTex );
        core.setPosition(50,250);
        core.setSize(100,600);
        group.add(core);

        enemyGroup = new Group();
        group.add(enemyGroup);
        enemyTex = new Texture("/enemy.png");
        enemyTimer = 2.0;
        enemySpeed = 100;

        explosionAnim = new Animation(
                "/explosion.png", 5,8, 0.02, false);


        healthLabel = new Label("Arial Bold", 25);
        healthLabel.fontColor = Color.GREENYELLOW;
        healthLabel.drawBorder = true;
        healthLabel.setPosition(410, 475);
        healthLabel.setText("Core Health:" + health);
        group.add(healthLabel);

        bulletGroup = new Group();
        group.add( bulletGroup );
        bulletTex = new Texture("/bullet.png");

        scoreLabel = new Label("Arial Bold", 25);
        scoreLabel.fontColor = Color.WHITESMOKE;
        scoreLabel.drawBorder = true;
        scoreLabel.setPosition(450, 35);
        scoreLabel.setText("Score:" + score);
        group.add(scoreLabel);

        LoseMessage = new Sprite();
        Texture LoseMessageTex = new Texture("/message-lose.png");
        LoseMessage.setTexture(LoseMessageTex);
        LoseMessage.setPosition(320,240);
        group.add(LoseMessage);
        LoseMessage.visible = false;


    }

    public void update()
    {
        if (LoseMessage.visible)
        return;

        if (input.isKeyPressed("RIGHT"))
            player.physics.accelerateAtAngle(0);

        if (input.isKeyPressed("LEFT"))
            player.physics.accelerateAtAngle(180);

        if (input.isKeyPressed("UP"))
            player.physics.accelerateAtAngle(270);

        if (input.isKeyPressed("DOWN"))
            player.physics.accelerateAtAngle(90);

        if ( input.isKeyJustPressed("SPACE") && bulletGroup.size() < 1)
        {

            Sprite bullet = new Sprite();
            bullet.setTexture(bulletTex);
            bullet.alignToSprite(player);
            bullet.setPhysics(new Physics(0, 400, 0));
            bullet.physics.setSpeed(400);
            bullet.physics.setMotionAngle(player.angle);
            bullet.addAction(Action.wrapToScreen(800, 600));
            bulletGroup.add(bullet);
            bullet.addAction(Action.delayFadeRemove(1, 0.5));
        }

        enemyTimer -= 1.0/60.0;
        if (enemyTimer < 0)
        {
            Sprite enemy = new Sprite();
            enemy.setTexture(enemyTex);
            double enemyY = Math.random() * 300 + 35;
            enemy.setPosition(700,enemyY);
            enemy.setPhysics(new Physics(0,600,0));
            enemy.physics.setSpeed(enemySpeed);
            enemy.physics.setMotionAngle(180);
            enemyGroup.add(enemy);
            enemyTimer = 2.0;
            enemySpeed += 10;
        }

        for (Entity EnemyE : enemyGroup.getList())
        {
            Sprite enemy = (Sprite)EnemyE;

            if (core.overlaps(enemy) && core.opacity > 0)
            {
                enemy.removeSelf();
                core.opacity -= 0.25;
                health -= 25;
                String healthcurrent = "Core Health:" + health;
                healthLabel.setText(healthcurrent);

                if (health <= 50)
                {
                    healthLabel.fontColor = Color.YELLOW;
                }

                if (health <= 25)
                {
                    healthLabel.fontColor = Color.RED;
                }

                Sprite explosion = new Sprite();
                explosion.setAnimation(
                        explosionAnim.clone() );
                explosion.alignToSprite(enemy);
                explosion.addAction( Action.animateThenRemove() );
                group.add( explosion );

                if (health == 0)
                {
                    LoseMessage.visible = true;
                }
            }

            // game over
            if (enemy.overlaps(player))
            {
                player.removeSelf();
                Sprite explosion = new Sprite();
                explosion.setAnimation(
                        explosionAnim.clone() );
                explosion.alignToSprite(player);
                explosion.addAction( Action.animateThenRemove() );
                group.add( explosion );
                LoseMessage.visible = true;
            }


            for (Entity bulletE : bulletGroup.getList())
            {
                Sprite bullet = (Sprite)bulletE;
                if (enemy.overlaps(bullet))
                {
                    score += 100;
                    String scorecurrent = "Points:" + score;
                    scoreLabel.setText(scorecurrent);

                    if (bullet.position.x > 640)
                    {
                        bulletGroup.remove(bullet);
                    }
                    enemyGroup.remove(enemy);

                    Sprite explosion = new Sprite();
                    explosion.setAnimation(
                            explosionAnim.clone() );
                    explosion.alignToSprite(enemy);
                    explosion.addAction( Action.animateThenRemove() );
                    group.add( explosion );

                    }
                }
            }
        }
}
