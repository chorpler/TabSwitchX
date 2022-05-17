package org.intellij.ideaplugins.tabswitchx.action;

import org.intellij.ideaplugins.tabswitchx.filefetcher.ChangedFilesInVcsFileFetcher;

public class NextVcsChangeAction extends ChangeTabAction {

  public NextVcsChangeAction() {
    super(new ChangedFilesInVcsFileFetcher());
  }

  @Override
  protected boolean moveUp() {
    return false;
  }
}
