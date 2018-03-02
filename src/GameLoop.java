import javax.swing.SwingUtilities;

public class GameLoop implements Runnable
{
	Thread _gameThread;
	MapPanel _mapPanel;

	private final double _ups = 60.0, _timeBetweenUpdates = 1000000000 / _ups, _targetFPS = 60,
			_timeBetweenRenders = 1000000000 / _targetFPS;
	private final int _maxUpdatesBeforeRender = 5;

	public GameLoop(MapPanel mapPanel)
	{
		_mapPanel = mapPanel;
	}

	public void startGame()
	{
		_gameThread = new Thread(this);
		_gameThread.start();
	}

	double lastUpdateTime = System.nanoTime(); // Store the time of the last
	double lastRenderTime = System.nanoTime(); // Store the time of the last
	double now;
	int updateCount;

	@Override
	public void run()
	{
		// double lastUpdateTime = System.nanoTime(); // Store the time of the
		// last
		// update call.
		// double lastRenderTime = System.nanoTime(); // Store the time of the
		// last
		// render call.
		// double now;
		// int updateCount;

		/**
		 * FPS Calculation Variables
		 */
		int lastSecondTime = (int) (lastUpdateTime / 1000000000);

		while (true)
		{
			now = System.nanoTime();
			updateCount = 0;

			/**
			 * Doing as many game updates as we currently need to.
			 */
			while (now - lastUpdateTime > _timeBetweenUpdates && updateCount < _maxUpdatesBeforeRender)
			{
				tick();
				lastUpdateTime += _timeBetweenUpdates;
				updateCount++;
			}

			// If for some reason an update takes forever, we don't want to do
			// an insane number of catchups.
			// If you were doing some sort of game that needed to keep EXACT
			// time, you would get rid of this.
			if (now - lastUpdateTime > _timeBetweenUpdates)
			{
				lastUpdateTime = now - _timeBetweenUpdates;
			}

			/**
			 * Render the current (updated) state of the game.
			 */
			render();
			lastRenderTime = now;

			// Update the frames we got.
			int thisSecond = (int) (lastUpdateTime / 1000000000);
			if (thisSecond > lastSecondTime)
			{
				lastSecondTime = thisSecond;
			}

			/**
			 * The timing mechanism. The thread sleeps within this while loop
			 * until enough time has passed and another update or render call is
			 * required.
			 */
			while (now - lastRenderTime < _timeBetweenRenders && now - lastUpdateTime < _timeBetweenUpdates)
			{
				Thread.yield(); // Yield until it has been at least the target
								// time between renders. This saves the CPU from
								// hogging.

				/**
				 * Preventing over-consumption of the system's CPU power.
				 */
				try
				{
					Thread.sleep(1);
				}
				catch (Exception e)
				{
				}

				now = System.nanoTime();
			}
		}
	}

	private void render()
	{
		// long startTime = System.nanoTime();
		SwingUtilities.invokeLater(() -> _mapPanel.repaint());
		// RealTimeSwing.invokeNow(() -> _mapPanel.repaint());
		// _mapPanel.paintImmediately(0, 0, _mapPanel.getWidth(),
		// _mapPanel.getHeight());
		// long estimatedTime = System.nanoTime() - startTime;
		// System.out.println("r:" + String.format("%.12f", (double)
		// estimatedTime / 1000000000));
	}

	private void tick()
	{
		//long startTime = System.nanoTime();
		SwingUtilities.invokeLater(() -> _mapPanel.checkMouse());
		SwingUtilities.invokeLater(() -> _mapPanel.getLogic().doLogic());
		// _mapPanel.checkMouse();
		// _mapPanel.getLogic().doLogic();
		// long estimatedTime = System.nanoTime() - startTime;
		// System.out.println("t:" + String.format("%.12f", (double)
		// estimatedTime / 1000000000));
	}
}
