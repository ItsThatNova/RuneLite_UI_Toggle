package com.uitoggle;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class UITogglePluginTest
{
	@SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(UiTogglePlugin.class);
		RuneLite.main(args);
	}
}
