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

import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.setDebug;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.log;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logDbg;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logDbg2;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logDbg3;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logDbg4;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logErr;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logWarn;
import static org.intellij.ideaplugins.tabswitchx.utilities.LogUtilities.logSuccess;

/**
 * Utilities for disambiguating the names of a list of {@link VirtualFile} or {@link File} objects.
 */
public class ListUtilities {

    /**
     * Sets terminal debug logging to true or false.
     * @param showDebugOutput True if you want to see debugging output, false otherwise.
     * @return True if debugging output will be shown, false otherwise.
     */
    public static boolean setDebug(boolean showDebugOutput) {
        return setDebug(showDebugOutput);
    }

    /**
     * Convert {@link List<String>} to a single string for output.
     * Each line of output will contain one element of the list.
     * Each element will also be surrounded by double quotes.
     *
     * @param list {@link List<String>} to be converted to a single String, one element per line.
     *
     * @return {String} The list represented as a single string.
     */
    public static String stringifyList(List<String> list) {
        List<String> fileNames = list.stream().map(f -> "\"" + f + "\"").collect(Collectors.toList());
        return String.join("\n", fileNames);
    }

    /**
     * Convert {@link List<File>} to a single string for output.
     * Each line of output will contain the full path of the {@link File} element.
     *
     * @param files {@link List<File>} to be converted to a single String, one file path per line.
     *
     * @return {String} The list of files represented as a single string.
     */
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

    /**
     * Convert {@link List<VirtualFile>} to a single string for output.
     * Each line of output will contain the full path of the {@link VirtualFile} element.
     *
     * @param files {@link List<VirtualFile>} to be converted to a single String, one file path per line.
     *
     * @return {String} The list of virtual files represented as a single string.
     */
    public static String stringifyListVirtualFiles(List<VirtualFile> files) {
        List<File> fileFiles = files.stream().map(vf -> new File(vf.getPath())).collect(Collectors.toList());
        return stringifyListFiles(fileFiles);
    }

    /**
     * Given a list of {@link VirtualFile}s, find the longest common prefix they all share and return it.
     * If all files are inside the same project directory, the project directory will be returned.
     *
     * @param files A {@link List<VirtualFile>} object to be checked for a common prefix.
     * @return String representing a common prefix shared by all the provided files. If there is none, or no files are provided, returns an empty string.
     */
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

    /**
     * Given a list of {@link VirtualFile}s, remove any prefix shared by all of them.
     * If all files are in a project, the project path will be removed, returning just the paths relative to the project root.
     *
     * @param files A {@link List<VirtualFile>} object to be stripped of a common prefix.
     * @return A {@link List<File>} list, where each {@link File} object has the relative path of the corresponding {@link VirtualFile} object.
     */
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

    /**
     * Customized version of the {@code VirtualFile.getName()} method. Given a {@link VirtualFile} and a depth, returns the file name plus {@code depth} parent directories.
     *
     * For example, if {@code file} represents the file '/code/project1/services/service1/ColorService.java', then:
     * {@code getNamePlus(file, 2) // returns 'services/service1/ColorService.java'}
     *
     * @param file A {@link VirtualFile} object.
     * @param depth Number of parent directories to return along with the filename.
     * @return The file name with {@code depth} parent directories prepended.
     */
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

    /**
     * Customized version of the {@code VirtualFile.getParent()} method. Given a {@link VirtualFile} object, returns the parent directory {@code depth} levels above it.
     *
     * For example, if {@code file} represents the file '/code/project1/services/service1/ColorService.java', then:
     * {@code getAncestor(file, 2) // returns a VirtualFile pointing to '/code/project1'}
     *
     * @param file A {@link VirtualFile} object.
     * @param depth The number of parent directories to look before returning the directory path.
     * @return The path to the parent directory at level {@code depth}.
     */
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

    /**
     * Get the number of parent directories a {@link VirtualFile} has in its path.
     *
     * Examples:
     *
     * If {@code file} represents the file '/code/project1/services/service1/ColorService.java', then:
     * {@code getDepth(file) // returns 4}
     *
     * If {@code file} represents the file 'services/service1/ColorService.java', then:
     * {@code getDepth(file) // returns 2}
     *
     * @param file A {@link VirtualFile} object.
     * @return Number of parent directories the provided {@link VirtualFile} has.
     */
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

    /**
     * Given a filename and a list of file paths, returns true if the filename is a duplicate of another file in the list.
     * Intended to be used to create a list of filenames that are as short as possible, but not ambiguous.
     *
     * @param fileName Name of the file to check.
     * @param allFilePaths List of file paths to be checked against.
     * @return True if the filename is a duplicate, false otherwise.
     */
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

    /**
     * Given a {@link VirtualFile} and a list of {@link VirtualFile}s, gets the shortest unique and unambiguous representation of the file's name.
     *
     * Examples:
     *
     * If the file is 'services/ColorService.java' and the list contains {@code [ 'services/ColorService.java' ]}, returns 'ColorService.java'.
     *
     * if the list contains {@code [ 'services/ColorService.java', 'components/ColorService.java' ]}, returns 'services/ColorService.java'.
     *
     * @param file A {@link VirtualFile} representing a file in a project.
     * @param allFiles List of all {@link VirtualFile}s to be checked against.
     * @return String representing the shortest non-ambiguous filename, with enough parent directories prepended to render it non-ambiguous.
     */
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

    /**
     * Given a list of {@link VirtualFile} objects representing all open files, returns non-ambiguous filename labels for all files in the list.
     *
     * @param files A list of {@link VirtualFile}s to be labeled non-ambiguously.
     * @return A list of {@link org.javatuples.Pair} tuples, where the first element is a {@link VirtualFile} and the second is a non-ambiguous filename label for the {@link VirtualFile}.
     */
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

    /**
     * Given a list of {@link File}s, find the longest common prefix they all share and return it.
     * If all files are inside the same project directory, the project directory will be returned.
     * Intended for unit testing; should be functionally identical to {@link getCommonPrefix}.
     *
     * @param files A {@link List<File>} object to be checked for a common prefix.
     * @return String representing a common prefix shared by all the provided files. If there is none, or no files are provided, returns an empty string.
     */
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

    /**
     * Given a list of {@link File}s, remove any prefix shared by all of them.
     * If all files are in a project, the project path will be removed, returning just the paths relative to the project root.
     * Intended for unit testing; should be functionally identical to {@link trimCommonPrefix}.
     *
     * @param files A {@link List<File>} object to be stripped of a common prefix.
     * @return A {@link List<File>} list, where each {@link File} object has the relative path of the corresponding input {@link File} object in the provided list.
     */
    public static void trimCommonPrefixFile(List<File> files) {
        String commonPrefix = getCommonPrefixFile(files);

        if (!commonPrefix.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                String trimmedPath = files.get(i).getPath().substring(commonPrefix.length());
                files.set(i, new File(trimmedPath));
            }
        }
    }

    /**
     * Customized version of the {@code File.getName()} method. Given a {@link File} and a depth, returns the file name plus {@code depth} parent directories.
     * Intended for unit testing; should be functionally identical to {@link getNamePlus}.
     *
     * For example, if {@code file} represents the file '/code/project1/services/service1/ColorService.java', then:
     * {@code getFileNamePlus(file, 2) // returns 'services/service1/ColorService.java'}
     *
     * @param file A {@link File} object.
     * @param depth Number of parent directories to return along with the filename.
     * @return The file name with {@code depth} parent directories prepended.
     */
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

    /**
     * Customized version of the {@code File.getParentFile()} method. Given a {@link File} object, returns the parent directory {@code depth} levels above it.
     *
     * Intended for unit testing; should be functionally identical to {@link getAncestor}.
     *
     * Example:
     *
     * If {@code file} represents the file '/code/project1/services/service1/ColorService.java', then:
     * {@code getAncestorFile(file, 2) // returns a VirtualFile pointing to '/code/project1'}
     *
     *
     * @param file A {@link File} object.
     * @param depth The number of parent directories to look before returning the directory path.
     * @return The path to the parent directory at level {@code depth}.
     */
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

    /**
     * Get the number of parent directories a {@link File} has in its path.
     *
     * Intended for unit testing; should be functionally identical to {@link getDepth}.
     *
     * Examples:
     *
     * If {@code file} represents the file '/code/project1/services/service1/ColorService.java', then:
     * {@code getDepth(file) // returns 4}
     *
     * If {@code file} represents the file 'services/service1/ColorService.java', then:
     * {@code getDepth(file) // returns 2}
     *
     * @param file A {@link VirtualFile} object.
     * @return Number of parent directories the provided {@link File} has.
     */
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

    /**
     * Given a filename and a list of file paths, returns true if the filename is a duplicate of another file in the list.
     * Intended to be used to create a list of filenames that are as short as possible, but not ambiguous.
     *
     * Intended for unit testing; should be functionally identical to {@link isDuplicate}.
     *
     * @param fileName Name of the file to check.
     * @param allFilePaths List of file paths to be checked against.
     * @return True if the filename is a duplicate, false otherwise.
     */
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

    /**
     * Given a {@link File} and a list of {@link File}s, gets the shortest unique and unambiguous representation of the file's name.
     *
     * Intended for unit testing; should be functionally identical to {@link getUniqueFilename}.
     *
     * Examples:
     *
     * If the file is 'services/ColorService.java' and the list contains {@code [ 'services/ColorService.java' ]}, returns 'ColorService.java'.
     *
     * if the list contains {@code [ 'services/ColorService.java', 'components/ColorService.java' ]}, returns 'services/ColorService.java'.
     *
     * @param file A {@link File} representing a file in a project.
     * @param allFiles List of all {@link File}s to be checked against.
     * @return String representing the shortest non-ambiguous filename, with enough parent directories prepended to render it non-ambiguous.
     */
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

    /**
     * Given a list of {@link File} objects representing all open files, returns non-ambiguous filename labels for all files in the list.
     * Intended for unit testing; should be functionally identical to {@link generateLabels}.
     *
     * @param files A list of {@link File}s to be labeled non-ambiguously.
     * @return A list of {@link org.javatuples.Pair} tuples, where the first element is a {@link File} and the second is a non-ambiguous filename label for the {@link File}.
     */
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
