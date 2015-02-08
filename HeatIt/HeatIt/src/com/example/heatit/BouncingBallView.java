package com.example.heatit;
//******************************************************************//
//developer.android.com training pages
//******************************************************************//


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
   
@SuppressLint("DrawAllocation") 
public class BouncingBallView extends View {
   private int xMin = 0;          // This view's bounds
   private int xMax;
   private int yMin = 0;
   private int yMax;
   private float ballRadius = 80;
   // Centre of the ball
   private float ballX = ballRadius + 40; 
   private float ballY = ballRadius + 60;
   private float ballSpeedX = 200; 
   private float ballSpeedY = 200;
   private RectF ballBounds;      
   private RectF ballBounds1;
   private RectF ballBounds2;
   
   //Different objects for different colors, style
   private Paint paint; 
   private Paint paint1;
   private Paint paint2;
   // Initialize objects
   public BouncingBallView(Context context) {
      super(context);
      ballBounds = new RectF();
      ballBounds1 = new RectF();
      ballBounds2 = new RectF();
      paint = new Paint();
      paint1 = new Paint();
      paint2 = new Paint();
   }
  
   // On draw function to draw the balls on canvas
   @Override
   protected void onDraw(Canvas canvas) {
      // Draw the ball
      ballBounds.set(ballX-ballRadius, ballY-ballRadius, ballX+ballRadius, ballY+ballRadius);
      ballBounds1.set(ballX-ballRadius+80, ballY-ballRadius+80, ballX+ballRadius+80, ballY+ballRadius+80);
      ballBounds2.set(ballX-ballRadius-80, ballY-ballRadius-80, ballX+ballRadius-80, ballY+ballRadius-80);
      paint.setColor(Color.WHITE);
      paint1.setColor(Color.WHITE);
      paint2.setColor(Color.WHITE);
      canvas.drawColor(Color.BLACK);


      Bitmap _scratch =  BitmapFactory.decodeResource(getResources(), R.drawable.black);
      canvas.drawBitmap(_scratch, 0, 0, null);
      canvas.drawOval(ballBounds, paint);
      canvas.drawOval(ballBounds1, paint1);
      canvas.drawOval(ballBounds2, paint2);
      update();
  
      // Delay
      try {  
         Thread.sleep(30);  
      } catch (InterruptedException e) { }
      
      invalidate();  
   }
   
   // Update position of ball according to speed parameters to show movement
   private void update() {
      // Get new position
      ballX += ballSpeedX;
      ballY += ballSpeedY;
      // Make the ball move within bounds
      if (ballX + ballRadius > xMax) {
         ballSpeedX = -ballSpeedX;
         ballX = xMax-ballRadius;
      } else if (ballX - ballRadius < xMin) {
         ballSpeedX = -ballSpeedX;
         ballX = xMin+ballRadius;
      }
      if (ballY + ballRadius > yMax) {
         ballSpeedY = -ballSpeedY;
         ballY = yMax - ballRadius;
      } else if (ballY - ballRadius < yMin) {
         ballSpeedY = -ballSpeedY;
         ballY = yMin + ballRadius;
      }
   }
   
   //Generated method to be called on size change
   @Override
   public void onSizeChanged(int w, int h, int oldW, int oldH) {
      // Set the movement bounds for the ball
      xMax = w-1;
      yMax = h-1;
   }
}
