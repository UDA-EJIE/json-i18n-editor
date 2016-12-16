package com.ejie.uda.jsonI18nEditor;

import java.awt.Toolkit;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.ejie.uda.jsonI18nEditor.util.MessageBundle;

/**
 * This class represents a menu item for searching a translation key.
 * 
 * @author Jacob
 */
public class FindTranslationMenuItem extends JMenuItem {
	private final static long serialVersionUID = 5207946396515235714L;

	public FindTranslationMenuItem(Editor editor, boolean enabled) {
		super(MessageBundle.get("menu.edit.find.translation.title"));
		setAccelerator(KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        addActionListener(e -> editor.showFindTranslationDialog());
        setEnabled(enabled);
	}
}