package com.ejie.uda.jsonI18nEditor;

import javax.swing.JMenuItem;

import com.ejie.uda.jsonI18nEditor.util.MessageBundle;

/**
 * This class represents a menu item for expanding all keys in of the translation tree.
 * 
 * @author Jacob
 */
public class ExpandTranslationsMenuItem extends JMenuItem {
	private final static long serialVersionUID = 7316102121075733726L;

	public ExpandTranslationsMenuItem(TranslationTree tree) {
        super(MessageBundle.get("menu.view.expand.title"));
     	addActionListener(e -> tree.expandAll());
	}
}