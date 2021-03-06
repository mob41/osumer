package com.github.mob41.osumer.ui;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

import com.github.mob41.osums.beatmap.OsuBeatmap;
import com.github.mob41.osums.beatmap.OsuSong;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class BeatmapPreviewDialogController implements Initializable {
	
	@FXML
	private ImageView image;
	
	@FXML
	private TabPane tab;
	
	@FXML
	private Tab beatmapTab;
	
	@FXML
	private VBox songBox;
	
	@FXML
	private VBox beatmapBox;
	
	private OsuSong map;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	protected void preview(OsuSong map) {
		this.map = map;
		
		DecimalFormat df = new DecimalFormat("#.##");
		List<Node> songNodes = songBox.getChildren();
		
		songNodes.add(new Label("Name: " + map.getName()));
		songNodes.add(new Label("Artist: " + map.getArtist()));
		songNodes.add(new Label("Title: " + map.getTitle()));
		songNodes.add(new Label("BPM: " + map.getBpm()));
		songNodes.add(new Label("Creator: " + map.getCreator()));
		songNodes.add(new Label("Source: " + map.getSource()));
		songNodes.add(new Label("Genre: " + map.getGenre()));
		songNodes.add(new Label("Rating: " + df.format(map.getRating()) + "%"));
		songNodes.add(new Label("Good/Bad Votes: " + map.getGoodRating() + "/" + map.getBadRating()));
		
		if (map instanceof OsuBeatmap) { //TODO Future use
			OsuBeatmap bmap = (OsuBeatmap) map;
			
			List<Node> beatmapNodes = beatmapBox.getChildren();
			
			beatmapNodes.add(new Label("Circle Size: " + df.format(bmap.getCircleSize())));
			beatmapNodes.add(new Label("Approach Rate: " + df.format(bmap.getApproachRate())));
			beatmapNodes.add(new Label("Accuracy: " + df.format(bmap.getAccuracy())));
			beatmapNodes.add(new Label("HP Drain: " + df.format(bmap.getHpDrain())));
			beatmapNodes.add(new Label("Star Difficulty: " + df.format(bmap.getStarDifficulty())));
			beatmapNodes.add(new Label("Success Rate: " + df.format(bmap.getSuccessRate()) + "%"));
		} else {
			beatmapTab.setDisable(true);
		}
		
		image.setImage(new Image("http:" + map.getThumbUrl()));
	}

}
