import React, { useState } from "react";
import { Upload, AlertCircle } from "lucide-react";

const FileUpload = ({ onFileUpload }) => {
  const [dragOver, setDragOver] = useState(false);
  const [uploading, setUploading] = useState(false);

  const handleDrop = async (e) => {
    e.preventDefault();
    setDragOver(false);

    const files = Array.from(e.dataTransfer.files);
    await handleFileUpload(files);
  };

  const handleFileSelect = async (e) => {
    const files = Array.from(e.target.files);
    await handleFileUpload(files);
  };

  const handleFileUpload = async (files) => {
    if (files.length === 0) return;

    setUploading(true);

    try {
      for (const file of files) {
        if (file.size > 50 * 1024 * 1024) {
          // 50MB limit
          throw new Error(
            `File "${file.name}" is too large. Maximum size is 50MB.`
          );
        }
        await onFileUpload(file);
      }
    } catch (error) {
      console.error("Upload failed:", error);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="space-y-4">
      <div
        className={`border-2 border-dashed rounded-lg p-8 text-center transition-colors ${
          dragOver ? "border-blue-500 bg-blue-50" : "border-gray-300"
        }`}
        onDragOver={(e) => {
          e.preventDefault();
          setDragOver(true);
        }}
        onDragLeave={() => setDragOver(false)}
        onDrop={handleDrop}
      >
        <Upload className="w-12 h-12 text-gray-400 mx-auto mb-4" />
        <p className="text-lg font-medium text-gray-700 mb-2">
          {uploading
            ? "Uploading files..."
            : "Drop files here or click to upload"}
        </p>
        <p className="text-gray-500 mb-4">
          Support for all file types up to 50MB
        </p>

        <input
          type="file"
          multiple
          className="hidden"
          id="file-upload"
          onChange={handleFileSelect}
          disabled={uploading}
        />

        <label
          htmlFor="file-upload"
          className={`inline-block px-6 py-2 rounded-lg cursor-pointer transition-colors ${
            uploading
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-blue-600 hover:bg-blue-700"
          } text-white`}
        >
          {uploading ? "Uploading..." : "Select Files"}
        </label>
      </div>

      {uploading && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <div className="flex items-center">
            <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-600 mr-3"></div>
            <span className="text-blue-800">
              Uploading files, please wait...
            </span>
          </div>
        </div>
      )}
    </div>
  );
};

export default FileUpload;
