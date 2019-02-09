package com.mygdx.game.contact_listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Player;

public class PlayerContactListener implements ContactListener {

    private Player player1;
    private Player player2;
    private PlayScreen playScreen;



    public PlayerContactListener(Player player1, Player player2, PlayScreen playScreen){
        this.player1 = player1;
        this.player2 = player2;
        this.playScreen = playScreen;
    }

    @Override
    public void beginContact(Contact contact) {

        //check for sides swap
        if (player1.isPlayer1Side & player1.player_body.getPosition().x > player2.player_body.getPosition().x){
            player1.isPlayer1Side = false;
            player2.isPlayer1Side = true;

            player1.turnSides();
            player2.turnSides();
        }
        if (player2.isPlayer1Side & player2.player_body.getPosition().x > player1.player_body.getPosition().x){
            player1.isPlayer1Side = true;
            player2.isPlayer1Side = false;

            player1.turnSides();
            player2.turnSides();
        }



        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();


        //do something with this!
        if(player1.player_body.getPosition().x > player2.player_body.getPosition().x - 185/StreetFighter.PPM & player1.player_body.getPosition().x < player2.player_body.getPosition().x + 185/StreetFighter.PPM ){
           // Gdx.app.log("Contact", "player1 on player2 collision");

            float distance = player2.player_body.getPosition().x - player1.player_body.getPosition().x;
            float maxSpeed = 200f/100f;

            float speed = maxSpeed - distance;
            //player1.setCurrentState(Player.State.COLIDING);
            //playScreen.setPlayerBodySpeedByState(player1);
            player1.currentSpeed = 0;

            if(player1.isPlayer1Side){
                player1.player_body.setLinearVelocity(-speed * 100,player1.player_body.getLinearVelocity().y);
                player2.player_body.setLinearVelocity(10,player2.player_body.getLinearVelocity().y);
            } else if(player2.isPlayer1Side){
                player2.player_body.setLinearVelocity(-speed * 100,player1.player_body.getLinearVelocity().y);
                player1.player_body.setLinearVelocity(10,player2.player_body.getLinearVelocity().y);
            }
        }

        if((fixtureA.getUserData() == "head" & fixtureB.getUserData() == "low") || (fixtureB.getUserData() == "low" & fixtureA.getUserData() == "low")){
            Fixture head = fixtureA.getUserData() == "head" ? fixtureA : fixtureB;
            Fixture low = head == fixtureA ? fixtureB : fixtureA;

            if(low.getBody().getUserData() == "player2") {
              //  Gdx.app.log("Contact", "player2 on player1");
            } else if(low.getBody().getUserData() == "player1"){
               // Gdx.app.log("Contact", "player1 on player2");
              //  player1.player_body.setLinearVelocity(-11, player1.player_body.getLinearVelocity().y);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
