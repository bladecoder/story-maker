package org.bladecoder.engineeditor.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bladecoder.engineeditor.Ctx;
import org.bladecoder.engineeditor.model.Project;
import org.bladecoder.engineeditor.ui.components.EditDialog;
import org.bladecoder.engineeditor.ui.components.FileInputPanel;
import org.bladecoder.engineeditor.ui.components.InputPanel;
import org.bladecoder.engineeditor.utils.DesktopUtils;
import org.bladecoder.engineeditor.utils.EditorLogger;
import org.bladecoder.engineeditor.utils.ImageUtils;

import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

@SuppressWarnings("serial")
public class CreateAtlasDialog extends EditDialog {

	private static final String INFO = "Package all the images in the selected dir to a new atlas";

	private static final String[] FILTERS = { "Linear", "Nearest", "MipMap",
			"MipMapLinearLinear", "MipMapLinearNearest", "MipMapNearestLinear",
			"MipMapNearestNearest" };

	private InputPanel name = new InputPanel("Atlas Name",
			"The name of the sprite atlas");
	private InputPanel dir = new FileInputPanel("Input Image Directory",
			"Select the output directory with the images to create the Atlas",
			true);

	private InputPanel filterMin = new InputPanel("Min Filter",
			"The filter when the texture is scaled down", FILTERS);
	private InputPanel filterMag = new InputPanel("Mag Filter",
			"The filter when the texture is scaled up", FILTERS);

	public CreateAtlasDialog(java.awt.Frame parent) {
		super(parent);

		centerPanel.add(name);
		centerPanel.add(dir);
		centerPanel.add(filterMin);
		centerPanel.add(filterMag);

		dir.setMandatory(true);
		name.setMandatory(true);

		filterMin.setText(FILTERS[0]);
		filterMag.setText(FILTERS[1]);

		setTitle("CREATE ATLAS");
		setInfo(INFO);

		init(parent);
	}

	@Override
	protected void ok() {
		dispose();

		Ctx.window.getScnCanvas().setMsg("Generating atlas...");

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				String msg = genAtlas();
				Ctx.window.getScnCanvas().setMsgWithTimer(msg, 2000);
			}
		});
		
		t.start();
	}

	@Override
	protected boolean validateFields() {
		boolean ok = true;

		if (!dir.validateField())
			ok = false;

		if (!name.validateField())
			ok = false;

		return ok;
	}

	private String genAtlas() {
		File inDir = new File(dir.getText());
		String outdir = Ctx.project.getProjectPath() + Project.ATLASES_PATH;
		List<Resolution> res = Ctx.project.getResolutions();
		String name = this.name.getText();
		String fMin = filterMin.getText();
		String fMag = filterMag.getText();

		Settings settings = new Settings();

		settings.pot = false;
		settings.paddingX = 2;
		settings.paddingY = 2;
		settings.duplicatePadding = true;
		settings.edgePadding = true;
		settings.rotation = false;
		settings.minWidth = 16;
		settings.minWidth = 16;
		settings.stripWhitespaceX = false;
		settings.stripWhitespaceY = false;
		settings.alphaThreshold = 0;

		if (fMin.equals("Linear"))
			settings.filterMin = TextureFilter.Linear;
		else if (fMin.equals("Nearest"))
			settings.filterMin = TextureFilter.Nearest;
		else if (fMin.equals("MipMap"))
			settings.filterMin = TextureFilter.MipMap;
		else if (fMin.equals("MipMapLinearLinear"))
			settings.filterMin = TextureFilter.MipMapLinearLinear;
		else if (fMin.equals("MipMapLinearNearest"))
			settings.filterMin = TextureFilter.MipMapLinearNearest;
		else if (fMin.equals("MipMapNearestLinear"))
			settings.filterMin = TextureFilter.MipMapNearestLinear;
		else if (fMin.equals("MipMapNearestNearest"))
			settings.filterMin = TextureFilter.MipMapNearestNearest;

		if (fMag.equals("Linear"))
			settings.filterMag = TextureFilter.Linear;
		else if (fMag.equals("Nearest"))
			settings.filterMag = TextureFilter.Nearest;
		else if (fMag.equals("MipMap"))
			settings.filterMag = TextureFilter.MipMap;
		else if (fMag.equals("MipMapLinearLinear"))
			settings.filterMag = TextureFilter.MipMapLinearLinear;
		else if (fMag.equals("MipMapLinearNearest"))
			settings.filterMag = TextureFilter.MipMapLinearNearest;
		else if (fMag.equals("MipMapNearestLinear"))
			settings.filterMag = TextureFilter.MipMapNearestLinear;
		else if (fMag.equals("MipMapNearestNearest"))
			settings.filterMag = TextureFilter.MipMapNearestNearest;

		settings.wrapX = Texture.TextureWrap.ClampToEdge;
		settings.wrapY = Texture.TextureWrap.ClampToEdge;
		settings.format = Format.RGBA8888;
		settings.alias = true;
		settings.outputFormat = "png";
		settings.jpegQuality = 0.9f;
		settings.ignoreBlankImages = true;
		settings.fast = false;
		settings.debug = false;

		int wWidth = Ctx.project.getWorld().getWidth();

		for (Resolution r : res) {
			settings.maxWidth = calcPOT(r.portraitWidth * 2);
			settings.maxHeight = calcPOT(r.portraitWidth * 2);

			EditorLogger.debug("ATLAS MAXWIDTH: " + settings.maxWidth);

			File inTmpDir = inDir;
			
			try {

				// Resize images to create atlas for diferent resolutions
				if (r.portraitWidth != wWidth) {
					inTmpDir = DesktopUtils.createTempDirectory();
										
					float scale = (float)r.portraitWidth / (float)wWidth;		
					
					ImageUtils.scaleDirFiles(inDir, inTmpDir, scale);
				}

				TexturePacker2.process(settings, inTmpDir.getAbsolutePath(),
						outdir + "/" + r.suffix, name + ".atlas");

				if (r.portraitWidth != wWidth) {
					DesktopUtils.removeDir(inTmpDir.getAbsolutePath());
				}
			} catch (IOException e) {
				EditorLogger.error(e.getMessage());
			}
		}

		return null;
	}

	private int calcPOT(int v) {
		v--;
		v |= v >> 1;
		v |= v >> 2;
		v |= v >> 4;
		v |= v >> 8;
		v |= v >> 16;
		v++;

		return v;
	}
}
