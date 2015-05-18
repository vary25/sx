package com.sxzq.oa.servcie;

public interface IConnectionStatusCallback {
	public void connectionStatusChanged(int connectedState, String reason);
}
