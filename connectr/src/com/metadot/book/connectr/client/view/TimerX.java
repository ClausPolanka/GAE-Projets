/** 
 * Copyright 2010 Daniel Guermeur and Amy Unruh
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   See http://connectrapp.appspot.com/ for a demo, and links to more information 
 *   about this app and the book that it accompanies.
 */
package com.metadot.book.connectr.client.view;

import com.google.gwt.user.client.Timer;

/**
 * This class can be used to poll for updates, using a timer.  This 'pull' model
 * is not currently used by Connectr, which instead uses the Channel API 
 * for 'push' notifications.
 */
public abstract class TimerX {
  
  /**
   * Gets called when the count down is == 0
   */
  public abstract void onAlarm();
  /**
   * Call every tick (like every sec, etc..).
   */
  public abstract void onTick();
  
  public abstract void onStart();
  
  TimerXHandler timerHandler;
  private long totalDurationMillis;
  private long countDownValueMillis;
  private long startTimeMillis;
  private Timer theTimer;
  private int tickEveryMillis;
  private long elapsedMs;
  private long timeLeftMs;

  /**
   * @param tickEveryMillis
   *        heart beat of the timer (e.g 1sec, .5 sec, etc...). Time in ms.
   * @param durationMillis
   *            duration of the timer in milliseconds
   * @param startNow
   *            whether the count down should start immediately or not
   * @param timerHandler
   *            handler that specifies what to do end the end of the timer
   */
  public TimerX(long durationMillis, int tickEveryMillis, boolean startNow) {
    this.totalDurationMillis = durationMillis;
    this.tickEveryMillis = tickEveryMillis;
    createTimer(totalDurationMillis);
    
    if (startNow)
      startNow();
  }

  public void cancelTimer() {
    theTimer.cancel();
    timeLeftMs = totalDurationMillis; // useful in case we restart it
  }

  /**
   * This timer will decrement the countdown till zero
   */
  private void createTimer(long countDownValueMillis) {
    this.countDownValueMillis = countDownValueMillis;
    theTimer = new Timer() {
      @Override
      public void run() {
        tick();
      }
    };
  }


  public long getElapsedMs() {
    return elapsedMs;
  }


  /**
   * Compute time left
   */
  private void tick() {
    elapsedMs = System.currentTimeMillis() - startTimeMillis;
    timeLeftMs = countDownValueMillis - elapsedMs;

    if (timeLeftMs < 0) {
      timeLeftMs = 0;
      theTimer.cancel();
      onAlarm();
    }
    
    onTick(); // this should be defined by the creator instance
  }

  public Long getTimeLeftMs() {
    return timeLeftMs;
  }

  public void restart() {
    this.countDownValueMillis = totalDurationMillis;
    startNow();
  }
  
  public void startNow() {
    startTimeMillis = System.currentTimeMillis(); 
    theTimer.scheduleRepeating(tickEveryMillis);
    onStart();
  }

}
