import React from "react";
import {
  Edit,
  Trash2,
  Eye,
  EyeOff,
  Lock,
  Key,
  ExternalLink,
} from "lucide-react";
import { formatDate } from "../../utils/helpers";

const CredentialList = ({
  credentials,
  onEditCredential,
  onDeleteCredential,
  onTogglePassword,
  visiblePasswords = {},
  onGetPassword,
}) => {
  console.log(
    `🔍 CredentialList rendered with ${credentials.length} credentials`
  );
  console.log(
    `📋 Credentials:`,
    credentials.map((c) => ({
      id: c.id,
      service: c.service,
      username: c.username,
    }))
  );
  console.log(`👁️ Visible passwords:`, visiblePasswords);

  if (credentials.length === 0) {
    return (
      <div className="text-center py-12">
        <Key className="w-12 h-12 text-gray-400 mx-auto mb-4" />
        <p className="text-gray-500">No credentials stored yet</p>
        <p className="text-sm text-gray-400 mt-2">
          Add your first credential to get started
        </p>
      </div>
    );
  }

  const handleTogglePassword = (credentialId) => {
    console.log(
      "CredentialList: calling onTogglePassword with",
      credentialId,
      typeof onTogglePassword
    );
    onTogglePassword(credentialId);
  };

  return (
    <div className="space-y-4">
      {credentials.map((credential) => (
        <div
          key={credential.id}
          className="bg-white border rounded-lg p-4 hover:shadow-sm transition-shadow"
        >
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3 flex-1">
              <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                <Lock className="w-5 h-5 text-blue-600" />
              </div>

              <div className="flex-1 min-w-0">
                <div className="flex items-center space-x-2">
                  <h3 className="font-medium text-gray-800">
                    {credential.service}
                  </h3>
                  {credential.url && (
                    <a
                      href={credential.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-blue-600 hover:text-blue-800"
                      title="Open website"
                    >
                      <ExternalLink className="w-4 h-4" />
                    </a>
                  )}
                </div>
                <p className="text-sm text-gray-600">{credential.username}</p>

                {visiblePasswords[credential.id] && (
                  <p className="text-sm font-mono bg-gray-100 px-2 py-1 rounded mt-1">
                    {visiblePasswords[credential.id]}
                  </p>
                )}

                {credential.notes && (
                  <p className="text-xs text-gray-500 mt-1">
                    {credential.notes}
                  </p>
                )}

                <p className="text-xs text-gray-400 mt-1">
                  Created: {formatDate(credential.createdDate)}
                </p>
              </div>
            </div>

            <div className="flex items-center space-x-2 ml-4">
              <button
                onClick={() => handleTogglePassword(credential.id)}
                className="p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                title={
                  visiblePasswords[credential.id]
                    ? "Hide password"
                    : "Show password"
                }
              >
                {visiblePasswords[credential.id] ? (
                  <EyeOff className="w-5 h-5" />
                ) : (
                  <Eye className="w-5 h-5" />
                )}
              </button>
              <button
                onClick={() => onEditCredential(credential)}
                className="p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                title="Edit credential"
              >
                <Edit className="w-5 h-5" />
              </button>
              <button
                onClick={() => onDeleteCredential(credential.id)}
                className="p-2 text-gray-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                title="Delete credential"
              >
                <Trash2 className="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default CredentialList;
