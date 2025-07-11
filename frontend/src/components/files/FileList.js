import React from "react";
import { File, Download, Trash2, FolderOpen } from "lucide-react";
import { formatFileSize, formatDate } from "../../utils/helpers";

const FileList = ({ files, onDeleteFile, onDownloadFile }) => {
  if (files.length === 0) {
    return (
      <div className="text-center py-12">
        <FolderOpen className="w-12 h-12 text-gray-400 mx-auto mb-4" />
        <p className="text-gray-500">No files uploaded yet</p>
        <p className="text-sm text-gray-400 mt-2">
          Upload your first file to get started
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {files.map((file) => (
        <div
          key={file.id}
          className="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
        >
          <div className="flex items-center space-x-3">
            <File className="w-8 h-8 text-blue-600" />
            <div>
              <p className="font-medium text-gray-800">{file.originalName}</p>
              <p className="text-sm text-gray-500">
                {formatFileSize(file.size)} • {formatDate(file.uploadDate)}
              </p>
            </div>
          </div>

          <div className="flex items-center space-x-2">
            <button
              onClick={() => onDownloadFile(file)}
              className="p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
              title="Download file"
            >
              <Download className="w-5 h-5" />
            </button>
            <button
              onClick={() => onDeleteFile(file.id)}
              className="p-2 text-gray-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
              title="Delete file"
            >
              <Trash2 className="w-5 h-5" />
            </button>
          </div>
        </div>
      ))}
    </div>
  );
};

export default FileList;
