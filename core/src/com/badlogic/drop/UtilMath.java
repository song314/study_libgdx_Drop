package com.badlogic.drop;

/**
 * Created by tangsong on 11/5/14.
 */
public class UtilMath {

    /**
     * // W / w = X / x
     * // x * (W / w) = X
     * // x = X * w / W
     *
     * @param deviceW
     * @param virtueW
     * @param device
     * @return
     */
    public static float getVirtue(float deviceW, float virtueW, float device) {
        return device * virtueW / deviceW;
    }
}
