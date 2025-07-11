import React from "react";
import { Edit, Trash2, FileText } from "lucide-react";
import { formatDate, truncateText } from "../../utils/helpers";

const NoteList = ({ notes, onEditNote, onDeleteNote }) => {
  if (notes.length === 0) {
    return (
      <div className="text-center py-12">
        <FileText className="w-12 h-12 text-gray-400 mx-auto mb-4" />
        <p className="text-gray-500">No notes created yet</p>
        <p className="text-sm text-gray-400 mt-2">
          Create your first note to get started
        </p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {notes.map((note) => (
        <div
          key={note.id}
          className="bg-white rounded-lg shadow-sm border p-6 hover:shadow-md transition-shadow"
        >
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-gray-800 truncate">
              {note.title}
            </h3>
            <div className="flex items-center space-x-1">
              <button
                onClick={() => onEditNote(note)}
                className="p-1 text-gray-600 hover:text-blue-600 rounded transition-colors"
                title="Edit note"
              >
                <Edit className="w-4 h-4" />
              </button>
              <button
                onClick={() => onDeleteNote(note.id)}
                className="p-1 text-gray-600 hover:text-red-600 rounded transition-colors"
                title="Delete note"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            </div>
          </div>

          <p className="text-gray-600 text-sm mb-4 line-clamp-3">
            {truncateText(note.content, 150)}
          </p>

          <div className="flex justify-between items-center text-xs text-gray-400">
            <span>Created: {formatDate(note.createdDate)}</span>
            {note.updatedDate !== note.createdDate && (
              <span>Updated: {formatDate(note.updatedDate)}</span>
            )}
          </div>
        </div>
      ))}
    </div>
  );
};

export default NoteList;
