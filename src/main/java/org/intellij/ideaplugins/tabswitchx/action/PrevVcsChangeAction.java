package org.intellij.ideaplugins.tabswitchx.action;

import org.intellij.ideaplugins.tabswitchx.filefetcher.ChangedFilesInVcsFileFetcher;

public class PrevVcsChangeAction extends ChangeTabAction {

  public PrevVcsChangeAction() {
    super(new ChangedFilesInVcsFileFetcher());
  }

  @Override
  protected boolean moveUp() {
    /* Move up in list */
    return true;
  }
}
