import React from "react";
import { useFiles } from "../../hooks/useFiles";
import FileUpload from "./FileUpload";
import FileList from "./FileList";

const FileSection = ({ user, showNotification }) => {
  console.log("FileSection mounted for user:", user);
  const {
    files,
    uploadFile,
    deleteFile,
    loading: filesLoading,
  } = useFiles(user);

  const handleFileUpload = async (file) => {
    try {
      await uploadFile(file);
      showNotification(`"${file.name}" uploaded successfully!`);
    } catch (error) {
      showNotification(`Failed to upload "${file.name}"`, "error");
    }
  };

  const handleFileDelete = async (fileId) => {
    if (window.confirm("Are you sure you want to delete this file?")) {
      try {
        await deleteFile(fileId);
        showNotification("File deleted successfully!");
      } catch (error) {
        showNotification("Failed to delete file", "error");
      }
    }
  };

  if (filesLoading) {
    return (
      <div className="flex justify-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <FileUpload onFileUpload={handleFileUpload} />
      <div className="bg-white rounded-lg shadow-sm border">
        <div className="p-6 border-b">
          <h2 className="text-xl font-semibold text-gray-800">
            Your Files ({files.length})
          </h2>
        </div>
        <div className="p-6">
          <FileList
            files={files}
            onDeleteFile={handleFileDelete}
            // onDownloadFile can be added if needed
          />
        </div>
      </div>
    </div>
  );
};

export default FileSection;
