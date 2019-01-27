package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;

public class Utils {

    private static float mediumDeltaTime = 0.020f;

    public static boolean isAnimationFinished(Animation animation, float stateTimer){
        return animation.isAnimationFinished(stateTimer);
    }

    public static boolean isOnFrame(Animation animation, int frame, float stateTimer){
        float totalFramesAmount = animation.getAnimationDuration()/animation.getFrameDuration();


        System.out.println(mediumDeltaTime*frame);
        System.out.println(stateTimer);

        if(stateTimer >= mediumDeltaTime * frame + mediumDeltaTime/2 & stateTimer < mediumDeltaTime * frame + animation.getFrameDuration())
            return true;
        else return false;
    }
}
