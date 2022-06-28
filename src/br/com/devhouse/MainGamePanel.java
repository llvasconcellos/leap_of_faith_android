package br.com.devhouse;

import br.com.devhouse.model.Ninja;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements Callback {
	
	private static final String TAG = MainGamePanel.class.getSimpleName();
	
	private MainThread thread;
	
	private Ninja ninja;

	public MainGamePanel(Context context) {
		super(context);
		
		//Adicionando o addCallback(this) para o SurfaceHolder para interceptar eventos
		getHolder().addCallback(this);
		
		//carrega o ninja
		ninja = new Ninja(BitmapFactory.decodeResource(getResources(), R.drawable.ninja_01), 100, 100);
		
		//Cria o processo de loop principal
		thread = new MainThread(getHolder(), this);
		
		//Fazer com que MainGamePanel ganhe foco para manipular eventos
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface sendo destruida");
		
		//diz para o thread terminar e aguarda o seu termino
		boolean retry = true;
		while(retry){
			try{
				thread.join();
				retry = false;
			}
			catch(InterruptedException e){
				//tente novamente finalizando o processo
			}
		}
		Log.d(TAG, "Thread foi finalizado limpamente");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			
			//delegando a manipulacao do evento ao ninja
			ninja.handleActionDown((int) event.getX(), (int) event.getY());
						
			//Verifica se está na parte mais inferior da tela para sair do programa.
			if(event.getY() > getHeight() - 50){
				thread.setRunning(false);
				((Activity)getContext()).finish();
			}
			else{
				Log.d(TAG, "Coords: x=" + event.getX() + ", y=" + event.getY());
			}
		}
		
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			//os gestos
			if(ninja.isTouched()){
				//O ninja foi segurado e esta sendo arrastado
				ninja.setX((int) event.getX());
				ninja.setY((int) event.getY());
			}
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP){
			//termino do toque
			if(ninja.isTouched()){
				ninja.setTouched(false);
			}
		}
		
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas){
		canvas.drawColor(Color.BLACK);
		
		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fundo_noite), 0, -1500, null);
				
		ninja.draw(canvas);
	}
}
