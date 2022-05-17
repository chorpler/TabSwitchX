package org.intellij.ideaplugins.tabswitchx.action;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.ideaplugins.tabswitchx.filefetcher.FileFetcher;

import java.util.List;

public abstract class ChangeTabAction extends TabAction {
  private final FileFetcher<VirtualFile> fileFetcher;

  protected ChangeTabAction(FileFetcher<VirtualFile> fileFetcher) {
    this.fileFetcher = fileFetcher;
  }

  @Override
  protected List<VirtualFile> getOpenFiles(Project project) {
    return fileFetcher.getFiles(project);
  }

  @Override
  protected boolean moveOnShow() {
    return false;
  }
}
