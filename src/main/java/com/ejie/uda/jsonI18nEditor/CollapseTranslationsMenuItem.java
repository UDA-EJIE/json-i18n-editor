package com.ejie.uda.jsonI18nEditor;

import javax.swing.JMenuItem;

import com.ejie.uda.jsonI18nEditor.util.MessageBundle;

/**
 * This class represents a menu item for collapsing all keys of the translation tree.
 * 
 * @author Jacob
 */
public class CollapseTranslationsMenuItem extends JMenuItem {
	private final static long serialVersionUID = 7885728865417192564L;

	public CollapseTranslationsMenuItem(TranslationTree tree) {
        super(MessageBundle.get("menu.view.collapse.title"));
     	addActionListener(e -> tree.collapseAll());
	}
}