package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;

public class Drop extends ApplicationAdapter {

    private ApplicationAdapter mAdapter = new NotingButBall();

    /**
     * 资源初始化
     */
    @Override
    public void create() {
        mAdapter.create();
    }

    @Override
    public void render() {
        mAdapter.render();
    }

    @Override
    public void dispose() {
        mAdapter.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
