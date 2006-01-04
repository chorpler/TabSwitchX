package org.intellij.ideaplugins.tabswitch;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

final class OpenFilesDialog extends IdeaDialog {

    private final JList list;
    private final int maxLength;
    private final Project project;

    OpenFilesDialog(Project project, String title, VirtualFile[] files) {
        super(project, title);
        this.project = project;
        list = new JList(files);
        maxLength = files.length;
        init();
    }

    protected JComponent createCenterPanel() {
        list.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dispose();
            }
        });
        list.setBorder(new EmptyBorder(5, 5, 5, 5));
        list.setSelectedIndex(0);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                dispose();
            }
        });
        list.setCellRenderer(new TabSwitchListCellRenderer(project));
        return list;
    }

    public void next() {
        int index = list.getSelectedIndex() + 1;
        if (index >= maxLength) {
            index = 0;
        }
        list.setSelectedIndex(index);
    }

    public void previous() {
        int index = list.getSelectedIndex() - 1;
        if (index < 0) {
            index = maxLength - 1;
        }
        list.setSelectedIndex(index);
    }

    public void select() {
        final VirtualFile virtualFile = (VirtualFile)list.getSelectedValue();
        final FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        if (virtualFile.isValid()) {
            fileEditorManager.openFile(virtualFile, true);
        }
    }
}