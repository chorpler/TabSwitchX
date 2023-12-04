package org.intellij.ideaplugins.tabswitchx.filefetcher;

import java.util.List;

import org.javatuples.Pair;

import com.intellij.openapi.project.Project;

/**
 * Should provide a way to fetch a list of files, of type {@code FILE}.
 */
public interface FileFetcher<FILE> {
  /**
   * @param project an idea project.
   *
   * @return List of files of type {@code FILE}, or empty. Not {@code null}.
   */
  List<FILE> getFiles(Project project);

  /**
   * Retrieve list of project files along with a string to use as the filename in the list.
   * @param project An IDEA project.
   * @return List of Pair<FILE,String> tuples, each containing a {@code FILE} and its display label. If no files are open, returns an empty list. Not {@code null}.
   */
  List<Pair<FILE,String>> getLabeledFiles(Project project);

  /**
   * Retrieve label for a VirtualFile in the project.
   * @param project An IDEA project.
   * @param file An IDEA VirtualFile representing a file open in a tab in the project.
   * @return {String} A string representing the display label for the provided file. Either file name, or the file name with enough parent directories prepended to make it unique in the file list. If the file is not found, returns an empty string. Not {@code null}.
   */
  String getFileLabel(Project project, FILE file);
}
