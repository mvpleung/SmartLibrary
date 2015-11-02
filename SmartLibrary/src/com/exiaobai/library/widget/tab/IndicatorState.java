package com.exiaobai.library.widget.tab;

/**
 * title 点击
 * 
 * @description
 * @author LiangZiChao
 * @Date 2014-9-1下午5:15:54
 * @Package com.xiaobai.xbtrip.view.tabs
 */
public interface IndicatorState {
	/**
	 * 当前的状态
	 */
	public enum State {
		MATRIX, FOCUS, NONE
	}

	/**
	 * 设置当前状态，派生类应该根据这个状态的变化来改变View的变化
	 * 
	 * @param state
	 *            状态
	 */
	public void setState(State state);

	/**
	 * 得到当前的状态
	 * 
	 * @return 状态
	 */
	public State getState();
}
