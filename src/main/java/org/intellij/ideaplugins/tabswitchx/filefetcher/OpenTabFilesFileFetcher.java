package org.intellij.ideaplugins.tabswitchx.filefetcher;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.impl.EditorHistoryManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import org.intellij.ideaplugins.tabswitchx.utilities.ListUtilities;
import org.javatuples.Pair;

/**
 * Creates a list of {@link VirtualFile} by fetching all the files that are open in tabs in current project.
 */
public class OpenTabFilesFileFetcher implements FileFetcher<VirtualFile> {

  @Override
  public List<VirtualFile> getFiles(Project project) {
    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
    EditorHistoryManager editorHistoryManager = EditorHistoryManager.getInstance(project);
    List<VirtualFile> virtualFiles = editorHistoryManager.getFileList();
    // System.out.println("OpenTabFiesFileFetcher.getFiles(): Found " + virtualFiles.size() + " open files");
    return getOpenFiles(fileEditorManager, ArrayUtil.reverseArray(virtualFiles.toArray(VirtualFile[]::new)));
  }

  /**
   * @param project An IDEA project.
   *
   * @return List of {@link org.javatuples.Pair} tuples, each containing a {@code VirtualFile} and its associated string label. Not {@code null}. List of open files in editor tabs.
   */
  @Override
  public List<Pair<VirtualFile,String>> getLabeledFiles(Project project) {
    List<VirtualFile> files = this.getFiles(project);
    List<Pair<VirtualFile,String>> filesAndLabels = ListUtilities.generateLabels(files);
    return filesAndLabels;
  }

  /**
   * Retrieve label for a VirtualFile in the project.
   * @param project An IDEA project.
   * @param file An IDEA VirtualFile representing a file open in a tab in the project.
   * @return {String} A string representing the display label for the provided file. Either file name, or the file name with enough parent directories prepended to make it unique in the file list. If the file is not found, returns an empty string. Not {@code null}.
   */
  @Override
  public String getFileLabel(Project project, VirtualFile file) {
    String result = file.getName();
    List<Pair<VirtualFile,String>> labeledFiles = this.getLabeledFiles(project);
    List<Pair<VirtualFile,String>> pairs = labeledFiles.stream().filter(p -> p.getValue0() == file).collect(Collectors.toList());
    if(pairs.size() == 1) {
      Pair<VirtualFile,String> pair = pairs.get(0);
      result = pair.getValue1();
    }
    return result;
  }

  private List<VirtualFile> getOpenFiles(FileEditorManager fileEditorManager, VirtualFile[] recentFiles) {
    List<VirtualFile> openFiles = new LinkedList<>();
    // int editorTabLimit = UISettings.getInstance().EDITOR_TAB_LIMIT;
    int editorTabLimit = UISettings.getInstance().getEditorTabLimit();
    for (VirtualFile file : recentFiles) {
    // for (VirtualFile file : recentFiles) {
      if (openFiles.size() <= editorTabLimit && fileEditorManager.isFileOpen(file) && !openFiles.contains(file)) {
        openFiles.add(file);
      }
    }
    // System.out.println("OpenTabFilesFileFetcher.getOpenFiles(): Found " + openFiles.size() + " tabs");
    return openFiles;
  }
}
