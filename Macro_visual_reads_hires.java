// Go through all steps within the QuadraNiftiTool. 
// Then place the files within the mr2pet_hires folder and run this macro script in ImageJ.

// Open a dialog asking the user to select the PET folder that contains the files to rename
petdir = getDirectory("Select PET folder (sub-..._totalbody...)");

// Get a list of all filenames inside the selected PET directory
list = getFileList(petdir);

// Create an empty variable that will later store the subject ID (e.g., sub-TAP01-0XX)
subject = "";

// STEP 1: rename PET files
// Loop through every file in the folder
for (i = 0; i < list.length; i++) {
    name = list[i]; // Store the current filename from the list
    newname = name; // Create a copy of the filename that will be modified

    // Remove "pet_" from the beginning of the filename if it exists
    if (startsWith(newname, "pet_"))
        newname = substring(newname, 4); // substring(4) removes the first 4 characters ("pet_")

    // Check whether the filename contains "_BRAIN"
    if (indexOf(newname, "_BRAIN") != -1) {
        newname = replace(newname, "_BRAIN", ""); // Remove "_BRAIN" from the filename
        newname = replace(newname, "totalbody", "brain"); // Replace the word "totalbody" with "brain"
    }

    // Extract the subject ID from the filename
    // Only do this once (when the subject variable is still empty)
    if (subject == "" && indexOf(newname, "sub-TAP") != -1) {
        start = indexOf(newname, "sub-TAP"); // Find the position where "sub-TAP" begins in the filename
        subject = substring(newname, start, start+13); // Extract 13 characters starting from that position. Example result: "sub-TAP01-0XX"
    }

    // If the filename has changed compared to the original
    if (newname != name)
        File.rename(petdir + name, petdir + newname); // Rename the file in the folder using the new filename
}

// STEP 2: find mr2pet_hires folder automatically

// Extract the part of the path before the "pet" folder
root = substring(petdir, 0, indexOf(petdir, "pet")); // This gives the base folder ending in BL\
mrdir = root + "anat/processed/mr2pet_hires/"; // Construct the full path to the MR2PET folder

// STEP 3: rename MRI2PET files
if (File.exists(mrdir)) { // Check whether the mr2pet_hires folder actually exists

    if (File.exists(mrdir + "MRI2PET.nii")) // If the file MRI2PET.nii exists
        File.rename(mrdir + "MRI2PET.nii", mrdir + subject + "_mr2pet_hires.nii"); // Rename it using the subject ID

    if (File.exists(mrdir + "MRI2PET.img")) // If the file MRI2PET.img exists
        File.rename(mrdir + "MRI2PET.img", mrdir + subject + "_mr2pet_hires.img"); // Rename it using the subject ID

    if (File.exists(mrdir + "MRI2PET.hdr")) // If the file MRI2PET.hdr exists
        File.rename(mrdir + "MRI2PET.hdr", mrdir + subject + "_mr2pet_hires.hdr"); // Rename it using the subject ID

    print("MRI2PET files renamed for " + subject); // Print a confirmation message in the ImageJ log window

} else { // If the folder was not found, print a warning message
    print("mr2pet_hires folder not found.");
}
