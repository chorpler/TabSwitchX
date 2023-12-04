package org.intellij.ideaplugins.tabswitchx.utilities;

import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

public class ListUtilitiesTest {

  @org.testng.annotations.BeforeMethod
  public void setUp() {
  }

  @org.testng.annotations.AfterMethod
  public void tearDown() {
  }

  @Test
  public void testGetFileNamePlus() {
    String testName = "testing/project/component1/tools/TestSubClass1.java";
    File file = new File(testName);
    List<String> allExpected = new ArrayList<>(Arrays.asList(
      "TestSubClass1.java",
      "tools/TestSubClass1.java",
      "component1/tools/TestSubClass1.java",
      "project/component1/tools/TestSubClass1.java",
      "testing/project/component1/tools/TestSubClass1.java"
    ));
    for(String expected : allExpected) {
      int i = allExpected.indexOf(expected);
      String result = ListUtilities.getFileNamePlus(file, i);
      assertEquals(result, expected);
    }

    String expected = "";
    String result = ListUtilities.getFileNamePlus(null, 0);
    assertEquals(result, expected);

    result = ListUtilities.getFileNamePlus(file, allExpected.size());
    assertEquals(result, testName);
  }

  @Test
  public void testGetAncestorFile() {
    String testName = "java/project/component1/tools/TestSubClass1.java";
    File file = new File(testName);
    List<String> allExpected = new ArrayList<>(Arrays.asList(
      "java/project/component1/tools",
      "java/project/component1",
      "java/project",
      "java",
      null
    ));
    for(String expected : allExpected) {
      int i = allExpected.indexOf(expected);
      File resultFile = ListUtilities.getAncestorFile(file, i);
      String result = resultFile == null ? null : resultFile.getPath();
      assertEquals(result, expected);
    }

    File result = ListUtilities.getAncestorFile(file, allExpected.size());
    assertNull(result);

    file = null;
    File result2 = ListUtilities.getAncestorFile(file, 0);
    assertNull(result2);
  }

  @Test
  public void testGetDepthFile() {
    List<String> allFilePaths = new ArrayList<>(Arrays.asList(
      "testing/project/component1/tools/TestSubClass1.java",
      "testing/project/component1/utils/TestSubClass1.java",
      "testing/project/component2/tools/TestSubClass1.java",
      "testing/project/component2/utils/TestSubClass1.java",
      "testing/project/TestClass1.java",
      "testing/project/TestClass2.java",
      "testing/project/TestClass3.java",
      "TestClass4.java",
      null
    ));
    List<Integer> expectedDepths = new ArrayList<>(Arrays.asList(4, 4, 4, 4, 2, 2, 2, 0, -1));
    for(String filePath : allFilePaths) {
      int i = allFilePaths.indexOf(filePath);
      File file = filePath != null ? new File(filePath) : null;
      int expected = expectedDepths.get(i);
      int result = ListUtilities.getDepthFile(file);
      assertEquals(result, expected);
    }
  }

  @Test
  public void testIsDuplicate() {
    List<String> allFilePaths = new ArrayList<>(Arrays.asList(
      "component1/tools/TestClass1.java",
      "component1/utils/TestClass1.java",
      "component2/tools/TestClass1.java",
      "component2/utils/TestClass1.java",
      "TestClass1.java",
      "TestClass2.java",
      "TestClass3.java"
    ));
    String testFileName = allFilePaths.get(0);
    boolean isDuplicate = ListUtilities.isDuplicate(testFileName, allFilePaths);
    boolean expected = false;
    assertEquals(isDuplicate, expected);
  }

  @Test
  public void testGetUniqueFilenameFile() {
    List<String> allFilePaths = new ArrayList<>(Arrays.asList(
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component1/tools/TestClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component1/utils/TestClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component2/tools/TestClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component2/utils/TestClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/TestClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/TestClass2.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/TestClass3.java"
    ));
    List<File> files = allFilePaths.stream().map(File::new).collect(Collectors.toList());
    File file = new File(allFilePaths.get(0));
    String uniqueName = ListUtilities.getUniqueFilenameFile(file, files);
    String expected = "component1/tools/TestClass1.java";
    assertEquals(uniqueName, expected);
  }

  @org.testng.annotations.Test
  public void testGetUniqueFilenameFile2() {
    List<String> allFilePaths = new ArrayList<>(Arrays.asList(
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component1/tools/TestSubClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component1/utils/TestSubClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component2/tools/TestSubClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component2/tools/TestClassUnique.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component2/utils/TestSubClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/TestClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/TestClass2.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/TestClass3.java"
    ));
    List<File> allFiles = allFilePaths.stream().map(File::new).collect(Collectors.toList());
    List<String> expectedResults = new ArrayList<>(Arrays.asList(
      "component1/tools/TestSubClass1.java",
      "component1/utils/TestSubClass1.java",
      "component2/tools/TestSubClass1.java",
      "TestClassUnique.java",
      "component2/utils/TestSubClass1.java",
      "TestClass1.java",
      "TestClass2.java",
      "TestClass3.java"
    ));
    for(File f : allFiles) {
      String result = ListUtilities.getUniqueFilenameFile(f, allFiles);
      int idx = allFiles.indexOf(f);
      String expected = expectedResults.get(idx);
      assertEquals(result, expected);
    }
  }

  @Test
  public void testGenerateLabelsFile() {
    List<String> allFilePaths = new ArrayList<>(Arrays.asList(
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component1/tools/TestClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component1/utils/TestClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component2/tools/TestClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/component2/utils/TestClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/TestClass1.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/TestClass2.java",
      "/Users/m243189/idea/projects/PluginTestingProject/src/edu/mayo/mprc/intellij/plugin/testing/project/TestClass3.java"
    ));
    List<String> expectedResults = new ArrayList<>(Arrays.asList(
      "component1/tools/TestClass1.java",
      "component1/utils/TestClass1.java",
      "component2/tools/TestClass1.java",
      "component2/utils/TestClass1.java",
      "TestClass1.java",
      "TestClass2.java",
      "TestClass3.java"
    ));
    List<File> files = allFilePaths.stream().map(File::new).collect(Collectors.toList());
    List<File> fileResults = ListUtilities.generateLabelsFile(files);
    List<String> results = fileResults.stream().map(File::getPath).collect(Collectors.toList());
    for(String result : results) {
      int i = results.indexOf(result);
      String expected = expectedResults.get(i);
      assertEquals(result, expected);
    }
  }
}
