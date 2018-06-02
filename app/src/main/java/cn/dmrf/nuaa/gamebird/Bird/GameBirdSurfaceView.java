package cn.dmrf.nuaa.gamebird.Bird;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import cn.dmrf.nuaa.gamebird.Gesture.GlobalBean;
import cn.dmrf.nuaa.gamebird.R;

import cn.dmrf.nuaa.gamebird.R;


public class GameBirdSurfaceView extends SurfaceView implements Callback, Runnable {

    private SurfaceHolder sfh;
    private Paint paint;

    private Thread th;
    private boolean flag;

    private Canvas canvas;
    private static int screenW, screenH;

    private static final int GAME_MENU = 0;
    private static final int GAMEING = 1;
    private static final int GAME_OVER = -1;

    private static int gameState = GAME_MENU;

    private int[] floor = new int[2];
    private int floor_width = 15;

    private int speed = 30;//左右速度（每次循环整个画面向左走speed个像素）

    private int[] level = new int[2];
    private int level_value = 0;

    private int[] bird = new int[2];
    private int bird_width = 10;
    private int bird_v = 0;
    private int bird_a = 2;
    private int bird_vUp = -16;


    private ArrayList<int[]> walls = new ArrayList<int[]>();
    private ArrayList<int[]> remove_walls = new ArrayList<int[]>();
    private int wall_w = 50;
    private int wall_h = 100;

    private int wall_step = 30;

    private Handler mHandler;

    private int count = 0;

    private Bitmap birdImg;
    private float birdImgX;
    private float birdImgY;

    private int speed_wall=50;

    public GameBirdSurfaceView(Context context, Handler mHandler) {

        super(context);
        this.mHandler = mHandler;
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();

        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(50);
        paint.setStyle(Style.STROKE); //空心
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenW = this.getWidth();
        screenH = this.getHeight();

        //System.out.println("=======screenW="+screenW+"=======screenH="+screenH);

        int[] wall = new int[]{screenW, (int) (Math.random() * (floor[1] - 2 * wall_h) + 0.5 * wall_h)};
        walls.add(wall);
        initGame();

        flag = true;

        th = new Thread(this);
        th.start();
    }



    private void initGame() {

        if (gameState == GAME_MENU) {

            floor[0] = 0;
            floor[1] = screenH - screenH / 5;

            level[0] = screenW / 2;
            level[1] = screenH / 5;

            level_value = 0;

            bird[0] = screenW / 3;//小鸟的初始x坐标
            bird[1] = screenH / 2;//小鸟的初始y坐标

            walls.clear();

            //dp to px
            floor_width = dp2px(15);

            speed = dp2px(3);

            bird_width = dp2px(10);
            bird_a = dp2px(2);
            bird_vUp = -dp2px(16);

            wall_w = dp2px(45);
            wall_h = dp2px(100);

            wall_step = wall_w * 4;
        }
    }


    private int dp2px(float dp) {
        int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
        return px;
    }

    public void drawBird() {
       // canvas.clipRect(birdImgX, birdImgY, birdImgX +  birdImg.getWidth(), posY + oddNumH);
       // canvas.drawBitmap(birdImg, birdImgX - 6 * oddNumW, posY, paint);
        canvas.restore();
        canvas.save();
        canvas.clipRect(birdImgX, birdImgY, birdImgX + birdImg.getWidth(), birdImgY + birdImg.getHeight() / 2);
        canvas.drawBitmap(birdImg, birdImgX, birdImgY - birdImg.getHeight() / 2, paint);

    }

    public void myDraw() {
        try {
            canvas = sfh.lockCanvas();
            if (canvas != null) {
                //clear
                canvas.drawColor(Color.BLACK);
                //background
                int floor_start = floor[0];
                while (floor_start < screenW) {
                    canvas.drawLine(floor_start, floor[1], floor_start + floor_width, floor[1], paint);
                    floor_start += floor_width * 2;
                }

                //wall
                for (int i = 0; i < walls.size(); i++) {
                    int[] wall = walls.get(i);

                    float[] pts = {
                            //wall[0], 0, wall[0], wall[1],
                            wall[0], wall[1] + wall_h, wall[0], floor[1],
                            //wall[0] + wall_w, 0, wall[0] + wall_w, wall[1],
                            wall[0] + wall_w, wall[1] + wall_h, wall[0] + wall_w, floor[1],
                            //wall[0], wall[1], wall[0] + wall_w, wall[1],
                            wall[0], wall[1] + wall_h, wall[0] + wall_w, wall[1] + wall_h
                            //,wall[0],floor[1], wall[0]+wall_w, floor[1]
                    };
                    canvas.drawLines(pts, paint);

                    //canvas.drawRect(wall[0], 0, wall[0]+wall_w, wall[1], paint);
                    //canvas.drawRect(wall[0], wall[1]+wall_h, wall[0]+wall_w, floor[1], paint);
                }

                //bird
                canvas.drawCircle(bird[0], bird[1], bird_width, paint);
//                birdImgX=bird[0];
//                birdImgY=bird[1];
//
//                birdImg= BitmapFactory.decodeResource(getResources(), R.drawable.bird);
//                canvas.drawBitmap(birdImg, birdImgX, birdImgY, paint);
//                //drawBird();
//                //level
                canvas.drawText(String.valueOf(GlobalBean.res), level[0], level[1], paint);

                //canvas.drawText(String.valueOf(GameBirdActivity.predis1), level[0], level[1], paint);

            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }

     /*---------------------------------
     * 绘制图片
     * @param       x屏幕上的x坐标
     * @param       y屏幕上的y坐标
     * @param       w要绘制的图片的宽度
     * @param       h要绘制的图片的高度
     * @param       bx图片上的x坐标
     * @param       by图片上的y坐标
     *
     * @return      null
     ------------------------------------*/

    public static void drawImage(Canvas canvas, Bitmap blt, int x, int y,
                                 int w, int h, int bx, int by) {
        Rect src = new Rect();// 图片 >>原矩形
        Rect dst = new Rect();// 屏幕 >>目标矩形

        src.left = bx;
        src.top = by;
        src.right = bx + w;
        src.bottom = by + h;

        dst.left = x;
        dst.top = y;
        dst.right = x + w;
        dst.bottom = y + h;
        // 画出指定的位图，位图将自动--》缩放/自动转换，以填补目标矩形
        // 这个方法的意思就像 将一个位图按照需求重画一遍，画后的位图就是我们需要的了
        canvas.drawBitmap(blt, null, dst, null);
        src = null;
        dst = null;
    }

    /**
     * 绘制一个Bitmap
     *
     * @param canvas 画布
     * @param bitmap 图片
     * @param x 屏幕上的x坐标
     * @param y 屏幕上的y坐标
     */

    public static void drawImage(Canvas canvas, Bitmap bitmap, int x, int y) {
        // 绘制图像 将bitmap对象显示在坐标 x,y上
        canvas.drawBitmap(bitmap, x, y, null);
    }


    public void up() {
        // bird[1] += bird_vUp;

        int temp=speed_wall;
        int i=count;
        if(walls.size()!=0) {
            int[] wall = walls.get(i++);
           // if(bird[0]>wall[0]-wall_w/2&&bird[0]<wall[0]+3*wall_w/2) {
                speed_wall=0;
                for (int a = 0; a < 3; a++)
                    bird[1] = wall[1];
            //}
           // else
              //  bird[i] += bird_vUp;
        }
        speed_wall=temp;

    }

    public void down() {
        // bird[1] -= bird_vUp;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int i = 0;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            switch (gameState) {
                case GAME_MENU:

                    Message msg1 = new Message();
                    msg1.what = 0;
                    msg1.obj = "start";
                    mHandler.sendMessage(msg1);

                    gameState = GAMEING;
                    //globalBean.BeginChangeWorld();
                    try {
                        Thread.sleep((long) (1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//					bird_v = bird_vUp;
//					break;
                case GAMEING:
                    if (event.getX() < screenW / 2) {
                        bird[1] += bird_vUp;
                        //bird_v = bird_vUp;//点击左边上升

                    } else {

                        bird[1] -= bird_vUp;
                        //bird_v+=bird_a;//点击右边下降
                    }

                    break;

                case GAME_OVER:

                    // globalBean.StopChangeWorld();
                    //bird down
                    if (bird[1] >= floor[1] - bird_width) {
                        Message msg2 = new Message();
                        msg2.what = 0;
                        msg2.obj = "stop";
                        mHandler.sendMessage(msg2);
                        gameState = GAME_MENU;
                        initGame();
                    }

                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            GameBirdActivity.instance.finish();
            System.exit(0);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private int move_step = 0;

    private void logic() {

        switch (gameState) {
            case GAME_MENU:

                break;
            case GAMEING:

                //bird
                //bird_v+=bird_a;//
                //bird[1] += bird_v;//小鸟上升bird_v像素，bird_v可正可负，如果是正的则小鸟向上走，否则小鸟向下走

                if (bird[1] > floor[1] - bird_width) {
                    bird[1] = floor[1] - bird_width;
                    gameState = GAME_OVER;
                }
                //top
                if (bird[1] <= bird_width) {
                    bird[1] = bird_width;
                  //  gameState = GAME_OVER;
                }

                //floor
                if (floor[0] < -floor_width) {
                    floor[0] += floor_width * 2;
                }
                floor[0] -= speed;

                //wall
                remove_walls.clear();
                for (int i = 0; i < walls.size(); i++) {
                    int[] wall = walls.get(i);
//                    if (bird[0] > wall[0] - 10 && bird[0] < wall[0] + wall_w + 10) {
//
//                        wall[0] -= speed * 2;
//                    } else {
                        bird[1] += 4;
                        wall[0] -= speed;


                        if (wall[0] < -wall_w) {
                            remove_walls.add(wall);
                        } else if (wall[0] - bird_width <= bird[0] && wall[0] + wall_w + bird_width >= bird[0]
                                && bird[1] >= wall[1] + wall_h - bird_width) {
                            gameState = GAME_OVER;
                            Message msg1 = new Message();
                            msg1.what = 0;
                            msg1.obj = "stop";
                            mHandler.sendMessage(msg1);

                        }

                        int pass = wall[0] + wall_w + bird_width - bird[0];
                        if (pass < 0 && -pass <= speed) {
                            level_value++;


                        }
                    //}
                }
                //out of screen
                if (remove_walls.size() > 0) {
                    walls.removeAll(remove_walls);
                }

                //new wall
                move_step += speed;
                if (move_step > wall_step) {
                    int[] wall = new int[]{screenW, (int) (Math.random() * (floor[1] - 2 * wall_h) + 0.5 * wall_h)};
                    walls.add(wall);
                    move_step = 0;
                }
                break;
            case GAME_OVER:
                //bird
                if (bird[1] < floor[1] - bird_width) {
                    bird_v += bird_a;
                    bird[1] += bird_v;
                    if (bird[1] >= floor[1] - bird_width) {
                        bird[1] = floor[1] - bird_width;
                    }
                } else {
                    GameBirdActivity.instance.showMessage(level_value);
                    gameState = GAME_MENU;
                    initGame();
                }
                break;

        }
    }

    @Override
    public void run() {
        while (flag) {
            long start = System.currentTimeMillis();
            myDraw();
            logic();
            long end = System.currentTimeMillis();
            try {
                if (end - start < speed_wall) {
                    Thread.sleep(speed_wall - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }
}
