package com.ejie.uda.jsonI18nEditor;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.ejie.uda.jsonI18nEditor.util.MessageBundle;

/**
 * This class represents a menu item for renaming a translation key.
 * 
 * @author Jacob
 */
public class RenameTranslationMenuItem extends JMenuItem {
	private final static long serialVersionUID = 5207946396515235714L;
	
	public RenameTranslationMenuItem(Editor editor, boolean enabled) {
        super(MessageBundle.get("menu.edit.rename.title"));
        setAccelerator(KeyStroke.getKeyStroke("F2"));
		addActionListener(e -> editor.renameSelectedTranslation());
		setEnabled(enabled);
	}
}