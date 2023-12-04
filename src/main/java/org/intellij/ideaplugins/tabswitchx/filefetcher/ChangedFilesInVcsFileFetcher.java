package org.intellij.ideaplugins.tabswitchx.filefetcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.ideaplugins.tabswitchx.utilities.ListUtilities;
import org.javatuples.Pair;

/**
 * Creates a list of {@link VirtualFile} by fetching all the modified files that are in the default change list of
 * chosen VCS.
 * <pre>
 * User: must
 * Date: 2012-06-02
 * </pre>
 */
public class ChangedFilesInVcsFileFetcher implements FileFetcher<VirtualFile> {

  private static final Comparator<VirtualFile> VIRTUAL_FILE_NAME_COMPARATOR = new Comparator<VirtualFile>() {
    @Override
    public int compare(VirtualFile vf1, VirtualFile vf2) {
      return vf1.getName().compareToIgnoreCase(vf2.getName());
    }
  };

  /**
   * @param project an idea project.
   *
   * @return Not {@code null}. Alphabetically sorted list of modified files.
   */
  @Override
  public List<VirtualFile> getFiles(Project project) {
    List<VirtualFile> changedFiles = new ArrayList<>();
    // int editorTabLimit = UISettings.getInstance().EDITOR_TAB_LIMIT;
    int editorTabLimit = UISettings.getInstance().getEditorTabLimit();
    int i = 0;
    for (Change change : getChanges(project)) {
      VirtualFile virtualFile = change.getVirtualFile();
      if (possibleToListVirtualFile(virtualFile)) {
        changedFiles.add(virtualFile);
        if (i++ == editorTabLimit) break;
      }
    }
    Collections.sort(changedFiles, VIRTUAL_FILE_NAME_COMPARATOR);
    return changedFiles;
  }

  /**
   * @param project An IDEA project.
   *
   * @return List of {@link org.javatuples.Pair} tuples, each containing a {@code VirtualFile} and its associated string label. Not {@code null}. Alphabetically sorted list of modified files.
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
    String result = "";
    List<Pair<VirtualFile,String>> labeledFiles = this.getLabeledFiles(project);
    List<Pair<VirtualFile,String>> pairs = labeledFiles.stream().filter(p -> p.getValue0() == file).collect(Collectors.toList());
    if(pairs.size() == 1) {
      Pair<VirtualFile,String> pair = pairs.get(0);
      result = pair.getValue1();
    }
    return result;
  }

  /**
   * @param project an idea project.
   *
   * @return {@code null} if no change list is available or if there are no changes currently made.
   */
  private Collection<Change> getChanges(Project project) {
    LocalChangeList defaultChangeList = ChangeListManager.getInstance(project).getDefaultChangeList();
    return defaultChangeList != null ? defaultChangeList.getChanges() : Collections.<Change>emptyList();
  }

  private boolean possibleToListVirtualFile(VirtualFile virtualFile) {
    return virtualFile != null && !virtualFile.isDirectory();
  }
}
