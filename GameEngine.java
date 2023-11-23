package pt.iscte.poo.sokobanstarter;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import java.util.Iterator;

import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

// Note que esta classe e' um exemplo - nao pretende ser o inicio do projeto, 
// embora tambem possa ser usada para isso.
//
// No seu projeto e' suposto haver metodos diferentes.
// 
// As coisas que comuns com o projeto, e que se pretendem ilustrar aqui, sao:
// - GameEngine implementa Observer - para  ter o metodo update(...)  
// - Configurar a janela do interface grafico (GUI):
//        + definir as dimensoes
//        + registar o objeto GameEngine ativo como observador da GUI
//        + lancar a GUI
// - O metodo update(...) e' invocado automaticamente sempre que se carrega numa tecla
//
// Tudo o mais podera' ser diferente!

public class GameEngine implements Observer {

	// Dimensoes da grelha de jogo
	public static final int GRID_HEIGHT = 10;
	public static final int GRID_WIDTH = 10;

	private static GameEngine INSTANCE; // Referencia para o unico objeto GameEngine (singleton)
	private ImageMatrixGUI gui; // Referencia para ImageMatrixGUI (janela de interface com o utilizador)
	private List<GameElement> gameElementsList; // Lista de imagens
	private Empilhadora bobcat; // Referencia para a empilhadora
	private int level_num; // Numero do nivel a carregar
	private int numberOfTargetsWithBoxes; // Numero de alvos com caixas
	private int numberOfTargets; // Numero de alvos

	private final int BATTERY_RELOAD = 50;
	private final int FIRST_LEVEL = 0;

	// Construtor - neste exemplo apenas inicializa uma lista de ImageTiles
	private GameEngine() {
		gameElementsList = new ArrayList<>();
	}

	// Implementacao do singleton para o GameEngine
	public static GameEngine getInstance() {
		if (INSTANCE == null)
			return INSTANCE = new GameEngine();
		return INSTANCE;
	}

	// Inicio
	public void start() {

		// Setup inicial da janela que faz a interface com o utilizador
		// algumas coisas poderiam ser feitas no main, mas estes passos tem sempre que
		// ser feitos!

		gui = ImageMatrixGUI.getInstance(); // 1. obter instancia ativa de ImageMatrixGUI
		gui.setSize(GRID_HEIGHT, GRID_WIDTH); // 2. configurar as dimensoes
		gui.registerObserver(this); // 3. registar o objeto ativo GameEngine como observador da GUI
		gui.go(); // 4. lancar a GUI

		this.level_num = FIRST_LEVEL;
		this.numberOfTargetsWithBoxes = 0;
		this.numberOfTargets = 0;

		createLevel(level_num); // criar o armazem
		sendImagesToGUI(); // enviar as imagens para a GUI

		// Escrever uma mensagem na StatusBar
		//gui.setStatusMessage("Sokoban" + " - Level " + level_num + " - " + "Battery: " + bobcat.getBattery());
	}

	// O metodo update() e' invocado automaticamente sempre que o utilizador carrega
	// numa tecla
	// no argumento do metodo e' passada uma referencia para o objeto observado
	// (neste caso a GUI)
	@Override
	public void update(Observed source) {
		int key = gui.keyPressed(); // obtem o codigo da tecla pressionada

		otherKeyInteractions(key);
		if (bobcat != null && Direction.isDirection(key)) {
			bobcatKeyMechanics(key);
			pickUpBattery();
			levelIncrementer();
		}

		gui.update(); // redesenha a lista de ImageTiles na GUI, tendo em conta as novas posicoes dos objetos
	}

	// --- FUNCTIONS FOR KEYS ---
	// outras funcoes das keys
	public void otherKeyInteractions(int key) {
		if (key == KeyEvent.VK_SPACE) {
			restartGame(FIRST_LEVEL);
		}
	}

	public void infoBox(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(null, infoMessage, titleBar, javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}

	//---- GAME MECHANICS ----
	private void levelIncrementer() {
		if (numberOfTargetsWithBoxes == numberOfTargets) {
			this.level_num++;
			if (this.level_num > 6) {
				infoBox("press SPACE for restart or ENTER for exit", "You Won the Game!!");
				System.exit(0);
			} else {
				infoBox("press SPACE for restart or ENTER for next level", "You Won!!");
			}
			restartGame(this.level_num++);
		}
	}

	public void restartGame(int level_num) {
		gui.clearImages(); // apaga todas as imagens atuais da GUI
		// apaga o conteudo das listas
		gameElementsList.clear();
		numberOfTargetsWithBoxes = 0;
		numberOfTargets = 0	;
		// reecria o primeiro nivel
		this.level_num = level_num;

		createLevel(level_num);
		sendImagesToGUI();
	}

	// funcao que cria o nivel
	private void createLevel(int level_num) {
		
		try {
			Scanner scanner = new Scanner(new File("levels\\level" + level_num + ".txt"));
			while (scanner.hasNextLine()) {
				for (int y = 0; y < GRID_HEIGHT; y++) { // loop pela altura da Tela
					String line = scanner.nextLine(); // meter a string/linha numa var
					for (int x = 0; x < line.length(); x++) {// loop pela a length da palavra que vai acabar por ser alargura da tela tambem
						GameElement gameElement = GameElement.create(line.charAt(x), new Point2D(x, y)); // criar o gameElement
						addGameElementToGUI(gameElement); // adicionar a lista correspondente
					}
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) { // se nao encontrar o ficheiro entao
			System.err.println("Erro: ficheiro/level não encontrado :(");
		}
		gui.update();
	}

	// funcao que dado um gameElement ele vai adicionar a lista correspondente
	// (tinha-se de fazer com gameElements é o porquê de ter feito asssim)
	private void addGameElementToGUI(GameElement gameElement) {
		if (gameElement instanceof Caixote || gameElement instanceof Palete 
		|| gameElement instanceof ParedeRachada || gameElement instanceof Bateria 
		|| gameElement instanceof Martelo || gameElement instanceof Buraco) {
			gameElementsList.add(gameElement);
			gameElementsList.add(GameElement.create(' ', gameElement.getPosition()));
		} else if (gameElement instanceof Empilhadora) {
			bobcat = (Empilhadora) gameElement;
			gameElementsList.add(bobcat);
			gameElementsList.add(GameElement.create(' ', gameElement.getPosition()));
		} else if (gameElement instanceof Alvo) {
			gameElementsList.add(gameElement);
			numberOfTargets++;
		} else {
			gameElementsList.add(gameElement);
		}
	}

	private void bobcatKeyMechanics(int key) {

		if (bobcatCollisionsChecker()) {
			bobcat.move(gui.keyPressed());
			bobcat.driveTo(Direction.directionFor(key));
			gui.setStatusMessage("Sokoban" + " - Level " + level_num + " - " + "Battery: " + bobcat.getBattery());
			//System.out.println(bobcat.getBattery()); // debug para ver a bateria se estácorreta
			System.out.println( "Numero total de Targets: " + numberOfTargets); // debug para ver o numero de alvos
			System.out.println( "Numero de Caixotes nos alvos: " + numberOfTargetsWithBoxes); // debug para ver o numero de alvos com caixotes
		} else {
			bobcat.move(key);
		}
	}

	/* funcao que checka se a empilhadora pode passar ou nao 
	[ se for um caixote ou uma palete entao move o caixote ou a palete + a empilhadora 
	, se for parede ou paredeRachada entao nao passa ] */
	private boolean bobcatCollisionsChecker() {
		for (GameElement ge : gameElementsList) {
			if (bobcat.nextPosition(gui.keyPressed()).equals(ge.getPosition())) {
				if (ge instanceof Caixote || ge instanceof Palete) {
					
					if (isCaixoteOnAlvo()){
						numberOfTargetsWithBoxes++;
					} else if (numberOfTargetsWithBoxes > 0 && numberOfTargetsWithBoxes <= numberOfTargets 
					&& collidableCollisionChecker(ge) && isSomethingAbove(ge.getPosition(), "Alvo")){
						numberOfTargetsWithBoxes--;
					}

					if (collidableCollisionChecker(ge)) {
						ge.setPosition(ge.getPosition().plus(Direction.directionFor(gui.keyPressed()).asVector()));;
						bobcat.addBattery(-1);
						return true;
					} else {
						return false; // se o caixote ou a palete nao mover entao , a empilhadora nao se move tambem
					}
				} else if (ge instanceof Parede || ge instanceof ParedeRachada) {
					return false;                    
				} 
			}
		}
		return true;
	}

	private boolean collidableCollisionChecker (GameElement ge) {
		for (GameElement next_ge : gameElementsList){
			if ( ge instanceof Caixote || ge instanceof Palete ) {
				if (next_ge instanceof Caixote || next_ge instanceof Palete || next_ge instanceof ParedeRachada || next_ge instanceof Parede) {
					if (ge.nextPosition(gui.keyPressed()).equals(next_ge.getPosition())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean isCaixoteOnAlvo() {
		for (GameElement ge1 : gameElementsList) {
			if (ge1 instanceof Alvo) {
				for (GameElement ge2 : gameElementsList) {
					if (ge2 instanceof Caixote && ge1.getPosition().equals(ge2.nextPosition(gui.keyPressed())) && bobcat.nextPosition(gui.keyPressed()).equals(ge2.getPosition())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isSomethingAbove(Point2D point , String name){
		for (GameElement ge : gameElementsList) {
			if (ge.getPosition().equals(point) && ge.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	// ---- ITEMS FUNCTS ----
	public void pickUpBattery() {
		Iterator<GameElement> iterator = gameElementsList.iterator();
		while (iterator.hasNext()) {
			GameElement item = iterator.next();
			if (item instanceof Bateria) {
				if (item.getPosition().equals(bobcat.getPosition())) {
					bobcat.addBattery(BATTERY_RELOAD);
					iterator.remove();
					gui.removeImage(item);
				}
			}
		}
	}

	// Envio das mensagens para a GUI - note que isto so' precisa de ser feito no
	// inicio
	// Nao e' suposto re-enviar os objetos se a unica coisa que muda sao as posicoes
	private void sendImagesToGUI() {

		gui.addImage(bobcat);

		for (GameElement ge : gameElementsList) {
			gui.addImage(ge);
		}

	}
}
