package org.intellij.ideaplugins.tabswitchx.component;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.intellij.openapi.fileEditor.FileEditorManager;
import org.intellij.ideaplugins.tabswitchx.TabSwitchProjectComponent;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBList;
import com.intellij.util.IconUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.javatuples.Pair;

import org.intellij.ideaplugins.tabswitchx.filefetcher.OpenTabFilesFileFetcher;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logWarn;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logDbg;

class ListComponentFactory {

  private Project project;

  ListComponentFactory(Project project) {
    this.project = project;
  }

  public JList create(JLabel pathLabel, Project project) {
    JList list = new JBList();
    if(project != null) {
      this.project = project;
    } else {
      logWarn("ListComponentFactory: project was null!");
    }
    OpenTabFilesFileFetcher otf = new OpenTabFilesFileFetcher();
    List<VirtualFile> allFiles = otf.getFiles(this.project);
    FileEditorManager manager = FileEditorManager.getInstance(project);
    VirtualFile[] openFiles = manager.getOpenFiles();
    List<VirtualFile> openFileList = Arrays.asList(openFiles);
    logDbg("ListComponentFactory: FileEditorManager says open files count is: " + openFileList.size());
    // List<VirtualFile> allFiles = new ArrayList<>(Arrays.asList(openFiles));
    List<String> allFileNames = new ArrayList<>(allFiles.size());
    for(VirtualFile f : allFiles) {
      allFileNames.add(f.getName());
    }
    logDbg("ListComponentFactory: all " + allFileNames.size() + " open files is: [ " + String.join(", ", allFileNames) + " ]");
    // list.setCellRenderer(new ListCellRendererWithColorFactory(project).create(project, allFiles));
    list.setCellRenderer(new ListCellRendererWithColorFactory(project).create());
    list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.getSelectionModel().addListSelectionListener(new ListSelectionListenerWithPathUpdaterFactory().create(list, pathLabel));
    // list.addMouseListener(new ListMouseListener(list));
    final ListMouseListener listMouseListener = new ListMouseListener(list);
    list.addMouseListener(listMouseListener);
    list.addMouseWheelListener(listMouseListener);
    // list.setMaximumSize(new Dimension(1000, 1000));
    // list.setPreferredSize(list.getSize());
    // int fileCount = list.getModel().getSize();
    return list;
  }

  private class ListMouseListener extends MouseAdapter {
    private final JList list;

    public ListMouseListener(JList list) {
      this.list = list;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      // System.out.println("mouseClicked(): Called with event: " + e.getID());
      int index = list.locationToIndex(e.getPoint());
      if (index != -1) {
        list.setSelectedIndex(index);
        TabSwitchProjectComponent.getHandler(project).closeAndOpenSelectedFile();
      }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      // System.out.println("mouseWheelMoved(): Called with event: " +  e.getID());
       String message;
       String newline = "\n";
       final JBList scrollPane = (JBList) list;
       int notches = e.getWheelRotation();
       scrollPane.getScrollableUnitIncrement(scrollPane.getVisibleRect(), SwingConstants.VERTICAL, 1);
       // if (notches < 0) {
       //     message = "Mouse wheel moved UP "
       //                  + -notches + " notch(es)" + newline;
       // } else {
       //     message = "Mouse wheel moved DOWN "
       //                  + notches + " notch(es)" + newline;
       // }
       // System.out.println("Notches: " + notches);
       int unitPixels = scrollPane.getScrollableUnitIncrement(scrollPane.getVisibleRect(), SwingConstants.VERTICAL, notches);
       int blocksToScroll = scrollPane.getScrollableBlockIncrement(scrollPane.getVisibleRect(), SwingConstants.VERTICAL, 1);
       Rectangle visibleRect = scrollPane.getVisibleRect();
       int pixelsToScroll = notches * unitPixels;

       if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
         // System.out.println("UNIT_SCROLL: Should scroll by " + pixelsToScroll + " px");
         visibleRect.add(0, pixelsToScroll);
         // scrollPane.getVisibleRect().add(0, unitsToScroll);
         int first = scrollPane.getFirstVisibleIndex();
         int last = scrollPane.getLastVisibleIndex();
         int length = scrollPane.getItemsCount();
         int newFirst = first + notches;
         int newLast = last + notches;
         if(newFirst < 0) {
           newFirst = 0;
         }
         if(newLast > length - 1) {
           newLast = length - 1;
         }
         scrollPane.ensureIndexIsVisible(newFirst);
         scrollPane.ensureIndexIsVisible(newLast);
         // System.out.println("New scroll bounds are indexes " + newFirst + " - " + newLast);
         // if(newFirst > length - 1) {
         //   new
         // }
         // if(unitsToScroll)

           // message += "    Scroll type: WHEEL_UNIT_SCROLL" + newline;
           // message += "    Scroll amount: " + e.getScrollAmount()
           //         + " unit increments per notch" + newline;
           // message += "    Units to scroll: " + e.getUnitsToScroll()
           //         + " unit increments" + newline;
           // message += "    Vertical unit increment: "
           //     + scrollPane.getScrollableUnitIncrement(scrollPane.getVisibleRect(), SwingConstants.VERTICAL, 1)
           //     + " pixels" + newline;
       } else { //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
         // System.out.println("BLOCK_SCROLL: Should scroll by " + blocksToScroll + " px");
           // message += "    Scroll type: WHEEL_BLOCK_SCROLL" + newline;
           // message += "    Vertical block increment: "
           //     + scrollPane.getScrollableBlockIncrement(scrollPane.getVisibleRect(), SwingConstants.VERTICAL, 1)
           //     + " pixels" + newline;
       }
       // saySomething(message, e);
      // System.out.println(message);
       // final Rectangle newRect =
       // scrollPane.scrollRectToVisible();

    }

  }

  /**
   * Simple ListCellRenderer factory. This is the default one to render the popped up list.
   */
  private static class ListCellRendererWithColorFactory {

    private static Project theProject;

    private ListCellRendererWithColorFactory(Project project) {
      theProject = project;
    }

    // private ListCellRendererWithColorFactory() {
    // }

    ListCellRenderer create() {
      // FileDocumentManager fdm = FileDocumentManager.getInstance();
      OpenTabFilesFileFetcher otf = new OpenTabFilesFileFetcher();
      List<Pair<VirtualFile,String>> allLabeledFiles = otf.getLabeledFiles(theProject);
      logDbg("ListCellRenderer.create(): Called with " + allLabeledFiles.size() + " tabs");
      return new ColoredListCellRenderer<VirtualFile>() {
        @Override
        protected void customizeCellRenderer(JList list,
                                             VirtualFile file,
                                             int index,
                                             boolean selected,
                                             boolean hasFocus) {
          setIcon(IconUtil.getIcon(file, Iconable.ICON_FLAG_READ_STATUS, theProject));
		  boolean isModified = FileDocumentManager.getInstance().isFileModified(file);
		  // String status = isModified ? "(*) " : "";
		  String status = isModified ? "*" : "";
      String displayName = otf.getFileLabel(theProject, file);
      String displayLabel = status + displayName;
      String filePath = file.getPath();
      logDbg("For file '" + filePath + "', unique name is: " + displayName);
          append(displayLabel, SimpleTextAttributes.fromTextAttributes(new TextAttributes(getForegroundColor(file, theProject),
                                                                                            null,
                                                                                            null,
                                                                                            EffectType.LINE_UNDERSCORE,
                                                                                            Font.PLAIN)));
        }
      };
    }

    private Color getForegroundColor(VirtualFile file, Project project) {
      return FileStatusManager.getInstance(project).getStatus(file).getColor();
    }
  }

  private static class ListSelectionListenerWithPathUpdaterFactory {

    private ListSelectionListenerWithPathUpdaterFactory() {
    }

    ListSelectionListener create(final JList list, final JLabel pathLabel) {
      return new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent event) {
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              updatePath(list, pathLabel);
            }
          });
        }
      };
    }

    private void updatePath(JList list, JLabel path) {
      // path.setText(getPathTextOrEmptyString(path, list.getSelectedValues()));
      path.setText(getPathTextOrEmptyString(path, list.getSelectedValuesList().toArray()));
    }

    private String getPathTextOrEmptyString(JLabel path, Object[] selectedValues) {
      return onlyOneFileIsSelected(selectedValues)
             ? getPathTextForSelectedFile(path, ((VirtualFile) selectedValues[0]).getParent())
             : "";
    }

    private boolean onlyOneFileIsSelected(Object[] selectedValues) {
      return selectedValues != null && selectedValues.length == 1;
    }

    private String getPathTextForSelectedFile(JLabel path, @Nullable VirtualFile parent) {
      if (parent == null) return "";
      String text = parent.getPresentableUrl();
      FontMetrics fontMetrics = path.getFontMetrics(path.getFont());
      while ((fontMetrics.stringWidth(text) > path.getWidth()) && (text.indexOf(File.separatorChar, 4) > 0)) {
        text = "..." + text.substring(text.indexOf(File.separatorChar, 4));
      }
      return text;
    }
  }
}
