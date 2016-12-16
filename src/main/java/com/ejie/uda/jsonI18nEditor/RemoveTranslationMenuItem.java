package com.ejie.uda.jsonI18nEditor;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.ejie.uda.jsonI18nEditor.util.MessageBundle;

/**
 * This class represents a menu item for removing a translation key.
 * 
 * @author Jacob
 */
public class RemoveTranslationMenuItem extends JMenuItem {
	private final static long serialVersionUID = 5207946396515235714L;
	
	public RemoveTranslationMenuItem(Editor editor, boolean enabled) {
        super(MessageBundle.get("menu.edit.delete.title"));
        setAccelerator(KeyStroke.getKeyStroke("DELETE"));
		addActionListener(e -> editor.removeSelectedTranslation());
		setEnabled(enabled);
	}
}