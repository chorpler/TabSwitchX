package org.intellij.ideaplugins.tabswitchx.utilities;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import static org.intellij.ideaplugins.tabswitchx.utilities.ConsoleColors.*;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.setDebug;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.log;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logDbg;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logDbg2;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logDbg3;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logDbg4;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logErr;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logWarn;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logSuccess;


public class ListUtilities {

    public static boolean setDebug(boolean showDebugOutput) {
        return setDebug(showDebugOutput);
    }

    public static String stringifyList(List<String> files) {
        List<String> fileNames = files.stream().map(f -> "\"" + f + "\"").collect(Collectors.toList());
        return String.join("\n", fileNames);
    }

    public static String stringifyListFiles(List<File> files) {
        StringBuilder out = new StringBuilder();
        String res;
        if(files == null) {
            out.append("(null)");
            out.append("\n");
        } else if(files.size() == 0) {
            out.append("(empty)");
            out.append("\n");
        } else {
            List<String> fileNames = files.stream().map(f -> f.getPath()).collect(Collectors.toList());
            for(String fn : fileNames) {
                out.append(fn);
                out.append("\n");
            }
        }
        res = out.toString();
        return res;
    }

    public static String stringifyListVirtualFiles(List<VirtualFile> files) {
        List<File> fileFiles = files.stream().map(vf -> new File(vf.getPath())).collect(Collectors.toList());
        return stringifyListFiles(fileFiles);
    }

    public static String getCommonPrefix(List<VirtualFile> files) {
        if (files == null || files.isEmpty()) {
            return "";
        }

        // Convert the File list to an array of strings (paths)
        String[] paths = files.stream().map(VirtualFile::getPath).toArray(String[]::new);

        // Find the shortest path to limit the comparison
        String shortest = paths[0];
        for (String path : paths) {
            if (path.length() < shortest.length()) {
                shortest = path;
            }
        }

        // Find the common prefix
        StringBuilder commonPrefix = new StringBuilder();
        for (int i = 0; i < shortest.length(); i++) {
            char currentChar = shortest.charAt(i);

            for (String path : paths) {
                if (path.charAt(i) != currentChar) {
                    return commonPrefix.toString();
                }
            }

            commonPrefix.append(currentChar);
        }

        return commonPrefix.toString();
    }

    public static List<File> trimCommonPrefix(List<VirtualFile> files) {
        String commonPrefix = getCommonPrefix(files);

        List<File> trimmedFiles = files.stream().map(vf -> new File(vf.getPath())).collect(Collectors.toList());

        if (!commonPrefix.isEmpty()) {
            for (int i = 0; i < trimmedFiles.size(); i++) {
                String trimmedPath = trimmedFiles.get(i).getPath().substring(commonPrefix.length());
                trimmedFiles.set(i, new File(trimmedPath));
            }
        }
        return trimmedFiles;
    }

    public static String getNamePlus(VirtualFile file, int depth) {
        if(file == null) {
            return "";
        }
        String name = file.getName();
        String sep = File.separator;
        String fp = file.getPath();
        logDbg("getNamePlus(): Called with depth " + depth + " and file: '" + fp + "'");
        VirtualFile parent = file.getParent();
        for(int i = 0; i < depth; i++) {
            if(parent == null) {
                break;
            }
            String pn = parent.getName();
            name = pn + sep + name;
            parent = parent.getParent();
        }
        logDbg("getNamePlus(): Depth " + depth + " result: " + name);
        return name;
    }

    public static VirtualFile getAncestor(VirtualFile file, int depth) {
        if(file == null) {
            return null;
        }
        VirtualFile ancestor = file.getParent();
        String fn = file.getPath();
        logDbg("getAncestor(): Called with depth " + depth + " and file: '" + fn + "'");
        for (int i = 0; i < depth; i++) {
            if(ancestor == null) {
                break;
            }
            ancestor = ancestor.getParent();
        }
        String ancestorPath = ancestor == null ? "(null)" : ancestor.getPath();
        logDbg("getAncestor(): depth " + depth + " gives: " + ancestorPath);
        return ancestor;
    }

    public static int getDepth(VirtualFile file) {
        int depth = 0;
        if(file == null) {
            return -1;
        }
        VirtualFile parent = file.getParent();
        while(parent != null) {
            depth++;
            parent = parent.getParent();
        }
        return depth;
    }

    public static boolean isDuplicate(String fileName, List<String> allFilePaths) {
        List<String> duplicates = allFilePaths.stream().filter(f -> f.endsWith(fileName)).collect(Collectors.toList());
        long count = duplicates.size();
        if(count > 1) {
            logDbg2("isDuplicate(): Found duplicates for filename '" + fileName + "':\n" + stringifyList(duplicates) + "\n\n");
        } else {
            logDbg2("isDuplicate(): No duplicates found for filename '" + fileName + "' " + "\n\n");
        }
        return count > 1;
    }

    public static String getUniqueFilename(VirtualFile file, List<VirtualFile> allFiles) {
        String fileName = file.getName();
        String originalFilePath = file.getPath();
        int index = allFiles.indexOf(file);
        logDbg("getUniqueFilename(): file '" + originalFilePath + "' is at index " + index);
        // List<File> allFilesCloned = allFiles.stream().map(f -> new File(f.getPath())).collect(Collectors.toList());
        List<File> allFilesCloned = trimCommonPrefix(allFiles);
        File newFile = allFilesCloned.get(index);
        String filePath = newFile.getPath();
        logDbg("getUniqueFilename(): new file '" + filePath + "' is at new index " + index);
        // Set<String> allFilePaths = allFilesCloned.stream().map(f -> f.getPath()).collect(Collectors.toSet());
        List<String> allFilePaths = allFilesCloned.stream().map(f -> f.getPath()).collect(Collectors.toList());
        // List<String> allFileNames = allFilesCloned.stream().map(f -> f.getName()).collect(Collectors.toList());
        List<String> allFileNames = allFilePaths;
        logDbg("getUniqueFilename(): List of filenames:\n" + stringifyList(allFileNames) + "\n\n");

        VirtualFile parent = file.getParent();
        String uniqueFileName = fileName;

        // while(parent != null) {
        // Keep prepending parent directory names until the file name is unique
        int depth = 1;
        while (isDuplicate(uniqueFileName, allFileNames) && uniqueFileName != filePath) {
            logDbg3("getUniqueFilename(): start iteration " + depth + ", uniqueFileName=" + uniqueFileName + ", list of filenames:\n" + stringifyList(allFileNames) + "\n");
            if (parent == null) {
                // No more parent directories, return the file name as is.
                break;
            }
            int i = allFileNames.indexOf(uniqueFileName);
            // uniqueFileName = parent.getName() + File.separator + uniqueFileName;
            // if(!isDuplicate4(uniqueFileName, allFileNames)) {
            //     break;
            // }
            uniqueFileName = getNamePlus(file, depth);
            // fileName = uniqueFileName;
            parent = parent.getParent();
            // allFileNames.set(i, uniqueFileName);
            logDbg3("getUniqueFilename(): end iteration " + depth + ", uniqueFileName=" + uniqueFileName + ", list of filenames:\n" + stringifyList(allFileNames) + "\n\n");
            depth++;
        }
        // if(parent == null) {
        // }

        logDbg("getUniqueFilename(): Returning unique file name '" + uniqueFileName + "'" + "\n\n");
        return uniqueFileName;
    }

    public static List<Pair<VirtualFile,String>> generateLabels(List<VirtualFile> files) {
        logDbg("generateLabels(): Incoming file list:\n" + stringifyListVirtualFiles(files));
        // List<File> filesCopy = files.stream().map(f -> new File(f.getPath())).collect(Collectors.toList());
        List<File> filesCopy = trimCommonPrefix(files);
        logDbg("\ngenerateLabels(): Trimmed file list:\n" + stringifyListFiles(filesCopy) + "\n\n");
        List<String> fileLabels = new ArrayList<>();
        int i = 0;
        for(File f : filesCopy) {
            i++;
            logDbg("generateLabels(): iteration " + i + " BEGIN, current file labels:\n" + stringifyList(fileLabels));
            String uniqueName = getUniqueFilenameFile(f, filesCopy);
            fileLabels.add(uniqueName);
            logDbg("generateLabels(): iteration " + i + " FINAL, current file labels:\n" + stringifyList(fileLabels) + "\n\n");
        }
        // List<File> newFiles = fileLabels.stream().map(f -> new File(f)).collect(Collectors.toList());
        logDbg("\ngenerateLabels(): Outgoing file labels:\n" + stringifyList(fileLabels));
        List<Pair<VirtualFile,String>> labeledFiles = new ArrayList<>(files.size());
        for(VirtualFile f : files) {
            int idx = files.indexOf(f);
            String fileLabel = fileLabels.get(idx);
            Pair<VirtualFile, String> fileAndLabel = new Pair<VirtualFile,String>(f, fileLabel);
            labeledFiles.add(fileAndLabel);
        }
        return labeledFiles;
    }

    public static String getCommonPrefixFile(List<File> files) {
        if (files == null || files.isEmpty()) {
            return "";
        }

        // Convert the File list to an array of strings (paths)
        String[] paths = files.stream().map(File::getPath).toArray(String[]::new);

        // Find the shortest path to limit the comparison
        String shortest = paths[0];
        for (String path : paths) {
            if (path.length() < shortest.length()) {
                shortest = path;
            }
        }

        // Find the common prefix
        StringBuilder commonPrefix = new StringBuilder();
        for (int i = 0; i < shortest.length(); i++) {
            char currentChar = shortest.charAt(i);

            for (String path : paths) {
                if (path.charAt(i) != currentChar) {
                    return commonPrefix.toString();
                }
            }

            commonPrefix.append(currentChar);
        }

        return commonPrefix.toString();
    }

    public static void trimCommonPrefixFile(List<File> files) {
        String commonPrefix = getCommonPrefixFile(files);

        if (!commonPrefix.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                String trimmedPath = files.get(i).getPath().substring(commonPrefix.length());
                files.set(i, new File(trimmedPath));
            }
        }
    }

    public static String getFileNamePlus(File file, int depth) {
        if(file == null) {
            return "";
        }
        String name = file.getName();
        String sep = File.separator;
        String fp = file.getPath();
        logDbg("getFileNamePlus(): Called with depth " + depth + " and file: '" + fp + "'");
        File parent = file.getParentFile();
        for(int i = 0; i < depth; i++) {
            if(parent == null) {
                break;
            }
            String pn = parent.getName();
            name = pn + sep + name;
            parent = parent.getParentFile();
        }
        logDbg("getFileNamePlus(): Depth " + depth + " result: " + name);
        return name;
    }

    public static File getAncestorFile(File file, int depth) {
        if(file == null) {
            return null;
        }
        File ancestor = file.getParentFile();
        String fn = file.getPath();
        logDbg("getAncestorFile(): Called with depth " + depth + " and file: '" + fn + "'");
        for (int i = 0; i < depth; i++) {
            if(ancestor == null) {
                break;
            }
            ancestor = ancestor.getParentFile();
        }
        String ancestorPath = ancestor == null ? "(null)" : ancestor.getPath();
        logDbg("getAncestorFile(): depth " + depth + " gives: " + ancestorPath);
        return ancestor;
    }

    public static int getDepthFile(File file) {
        int depth = 0;
        if(file == null) {
            return -1;
        }
        File parent = file.getParentFile();
        while(parent != null) {
            depth++;
            parent = parent.getParentFile();
        }
        return depth;
    }

    public static boolean isDuplicateFile(String fileName, List<String> allFilePaths) {
        List<String> duplicates = allFilePaths.stream().filter(f -> f.endsWith(fileName)).collect(Collectors.toList());
        long count = duplicates.size();
        if(count > 1) {
            logDbg2("isDuplicateFile(): Found duplicates for filename '" + fileName + "':\n" + stringifyList(duplicates) + "\n\n");
        } else {
            logDbg2("isDuplicateFile(): No duplicates found for filename '" + fileName + "' " + "\n\n");
        }
        return count > 1;
    }

    public static String getUniqueFilenameFile(File file, List<File> allFiles) {
        String fileName = file.getName();
        String originalFilePath = file.getPath();
        int index = allFiles.indexOf(file);
        logDbg("getUniqueFilenameFile(): file '" + originalFilePath + "' is at index " + index);
        List<File> allFilesCloned = allFiles.stream().map(f -> new File(f.getPath())).collect(Collectors.toList());
        trimCommonPrefixFile(allFilesCloned);
        File newFile = allFilesCloned.get(index);
        String filePath = newFile.getPath();
        logDbg("getUniqueFilenameFile(): new file '" + filePath + "' is at new index " + index);
        // Set<String> allFilePaths = allFilesCloned.stream().map(f -> f.getPath()).collect(Collectors.toSet());
        List<String> allFilePaths = allFilesCloned.stream().map(f -> f.getPath()).collect(Collectors.toList());
        // List<String> allFileNames = allFilesCloned.stream().map(f -> f.getName()).collect(Collectors.toList());
        List<String> allFileNames = allFilePaths;
        logDbg("getUniqueFilenameFile(): List of filenames:\n" + stringifyList(allFileNames) + "\n\n");

        File parent = file.getParentFile();
        String uniqueFileName = fileName;

        // while(parent != null) {
        // Keep prepending parent directory names until the file name is unique
        int depth = 1;
        while (isDuplicateFile(uniqueFileName, allFileNames) && uniqueFileName != filePath) {
            logDbg3("getUniqueFilenameFile(): start iteration " + depth + ", uniqueFileName=" + uniqueFileName + ", list of filenames:\n" + stringifyList(allFileNames) + "\n");
            if (parent == null) {
                // No more parent directories, return the file name as is.
                break;
            }
            int i = allFileNames.indexOf(uniqueFileName);
            // uniqueFileName = parent.getName() + File.separator + uniqueFileName;
            // if(!isDuplicate4(uniqueFileName, allFileNames)) {
            //     break;
            // }
            uniqueFileName = getFileNamePlus(file, depth);
            // fileName = uniqueFileName;
            parent = parent.getParentFile();
            // allFileNames.set(i, uniqueFileName);
            logDbg3("getUniqueFilenameFile(): end iteration " + depth + ", uniqueFileName=" + uniqueFileName + ", list of filenames:\n" + stringifyList(allFileNames) + "\n\n");
            depth++;
        }
        // if(parent == null) {
        // }

        logDbg("getUniqueFilenameFile(): Returning unique file name '" + uniqueFileName + "'" + "\n\n");
        return uniqueFileName;
    }

    public static List<File> generateLabelsFile(List<File> files) {
        LogUtilities.logDbg("generateLabelsFile(): Incoming file list:\n" + stringifyListFiles(files));
        List<File> filesCopy = files.stream().map(f -> new File(f.getPath())).collect(Collectors.toList());
        trimCommonPrefixFile(filesCopy);
        logDbg("\ngenerateLabelsFile(): Trimmed file list:\n" + stringifyListFiles(filesCopy) + "\n\n");
        List<String> fileLabels = new ArrayList<>();
        int i = 0;
        for(File f : filesCopy) {
            i++;
            logDbg("generateLabelsFile(): iteration " + i + " BEGIN, current file labels:\n" + stringifyList(fileLabels));
            String uniqueName = getUniqueFilenameFile(f, filesCopy);
            fileLabels.add(uniqueName);
            logDbg("generateLabelsFile(): iteration " + i + " FINAL, current file labels:\n" + stringifyList(fileLabels) + "\n\n");
        }
        List<File> newFiles = fileLabels.stream().map(f -> new File(f)).collect(Collectors.toList());
        logDbg("\ngenerateLabelsFile(): Outgoing file labels:\n" + stringifyListFiles(newFiles));
        return newFiles;
//        List<String> fileNames = files.stream().map(f -> f.getPath()).collect(Collectors.toList());
//        for(String fn : fileNames) {
//            String newfn = getUniqueFilename3(fn, fileNames);
//        }
//        return files;
    }
}
