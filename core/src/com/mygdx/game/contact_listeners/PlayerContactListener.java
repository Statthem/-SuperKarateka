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
        if (player1.isPlayer1Side & player1.getPlayer_body().getPosition().x > player2.getPlayer_body().getPosition().x){
            player1.isPlayer1Side = false;
            player2.isPlayer1Side = true;

            player1.turnSides();
            player2.turnSides();
        }
        if (player2.isPlayer1Side & player2.getPlayer_body().getPosition().x > player1.getPlayer_body().getPosition().x){
            player1.isPlayer1Side = true;
            player2.isPlayer1Side = false;

            player1.turnSides();
            player2.turnSides();
        }



        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();


        //Speedup player jump when jumping over other player
            if((fixtureA.getUserData() == "high" & fixtureB.getUserData() == "low") || (fixtureB.getUserData() == "high" & fixtureA.getUserData() == "low")){
                Fixture highFixture = fixtureA.getUserData() == "mid" ? fixtureA : fixtureB;
                Fixture lowFixture = highFixture == fixtureA ? fixtureB : fixtureA;

                if(lowFixture.getBody().getUserData() == "player2") {
                    if(player2.getCurrentSpeed() > 0)
                        player2.setCurrentSpeed((player2.getCurrentSpeed() + 0.75f));
                    if(player2.getCurrentSpeed() < 0)
                        player2.setCurrentSpeed((player2.getCurrentSpeed() - 0.75f));
                } else if(lowFixture.getBody().getUserData() == "player1"){
                    if(player1.getCurrentSpeed() > 0)
                    player1.setCurrentSpeed((player1.getCurrentSpeed() + 0.75f));
                    if(player1.getCurrentSpeed() < 0)
                        player1.setCurrentSpeed((player1.getCurrentSpeed() - 0.75f));
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
