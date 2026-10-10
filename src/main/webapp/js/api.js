/**
 * ProjectX - Core API Client & Global State Management
 */

const API_BASE = (() => {
  const path = window.location.pathname;
  if (path.startsWith("/projectx")) {
    return "/projectx/api";
  }
  return "/api";
})();

// ============================================================================
// Toast Notification Engine
// ============================================================================
const Toast = {
  container: null,

  init() {
    if (!this.container) {
      this.container = document.createElement("div");
      this.container.id = "toast-container";
      document.body.appendChild(this.container);
    }
  },

  show(message, type = "info", duration = 4000) {
    this.init();
    const toast = document.createElement("div");
    toast.className = `toast ${type}`;
    
    let icon = "ℹ️";
    if (type === "success") icon = "✅";
    if (type === "error") icon = "❌";
    if (type === "warning") icon = "⚠️";

    toast.innerHTML = `
      <div style="display:flex;align-items:center;gap:0.75rem;">
        <span>${icon}</span>
        <span style="font-weight:500;">${message}</span>
      </div>
      <button style="background:none;border:none;cursor:pointer;color:#94a3b8;font-size:1.1rem;margin-left:1rem;" onclick="this.parentElement.remove()">&times;</button>
    `;

    this.container.appendChild(toast);

    setTimeout(() => {
      if (toast.parentElement) {
        toast.style.opacity = "0";
        toast.style.transform = "translateX(100%)";
        toast.style.transition = "all 0.3s ease";
        setTimeout(() => toast.remove(), 300);
      }
    }, duration);
  },

  success(msg) { this.show(msg, "success"); },
  error(msg) { this.show(msg, "error"); },
  warning(msg) { this.show(msg, "warning"); },
  info(msg) { this.show(msg, "info"); }
};

// ============================================================================
// Modal Helpers
// ============================================================================
const Modal = {
  open(modalId) {
    const el = document.getElementById(modalId);
    if (el) el.classList.add("open");
  },
  close(modalId) {
    const el = document.getElementById(modalId);
    if (el) el.classList.remove("open");
  }
};

// ============================================================================
// Active Demo User Store
// ============================================================================
const UserStore = {
  STORAGE_KEY: "projectx_demo_user_id",

  getActiveId() {
    const val = localStorage.getItem(this.STORAGE_KEY);
    return val ? parseInt(val, 10) : 1; // Default to Aarav Sharma (ID 1)
  },

  setActiveId(id) {
    localStorage.setItem(this.STORAGE_KEY, id);
    window.dispatchEvent(new CustomEvent("projectx:userChanged", { detail: { studentId: id } }));
  },

  async initUserSelector(selectElementId = "demo-user-select") {
    const select = document.getElementById(selectElementId);
    if (!select) return;

    try {
      const resp = await API.students.getAll();
      if (resp && resp.data) {
        select.innerHTML = "";
        const activeId = this.getActiveId();

        resp.data.forEach(s => {
          const opt = document.createElement("option");
          opt.value = s.studentId;
          opt.textContent = `${s.fullName} (${s.department.split(" ")[0]})`;
          if (s.studentId === activeId) {
            opt.selected = true;
          }
          select.appendChild(opt);
        });

        // Set user avatar if present
        this.updateHeaderAvatar(resp.data.find(s => s.studentId === activeId));

        select.addEventListener("change", (e) => {
          const newId = parseInt(e.target.value, 10);
          this.setActiveId(newId);
          const chosen = resp.data.find(s => s.studentId === newId);
          this.updateHeaderAvatar(chosen);
          Toast.info(`Switched active demo student to ${chosen.fullName}`);
        });
      }
    } catch (e) {
      console.warn("Could not populate user switcher:", e);
    }
  },

  updateHeaderAvatar(student) {
    const avatarEl = document.getElementById("header-user-avatar");
    if (avatarEl && student && student.avatarUrl) {
      avatarEl.src = student.avatarUrl;
    }
  }
};

// ============================================================================
// REST API Client
// ============================================================================
const API = {
  async request(endpoint, options = {}) {
    const url = `${API_BASE}${endpoint}`;
    const defaultHeaders = {
      "Accept": "application/json"
    };

    if (options.body && typeof options.body === "object") {
      defaultHeaders["Content-Type"] = "application/json";
      options.body = JSON.stringify(options.body);
    }

    options.headers = { ...defaultHeaders, ...options.headers };

    try {
      const resp = await fetch(url, options);
      const json = await resp.json().catch(() => null);

      if (!resp.ok) {
        const errorMsg = (json && (json.message || json.error)) || `HTTP ${resp.status}: Request failed`;
        throw new Error(errorMsg);
      }
      return json;
    } catch (err) {
      console.error(`API Error on [${options.method || "GET"} ${endpoint}]:`, err);
      throw err;
    }
  },

  students: {
    getAll() {
      return API.request("/students");
    },
    getById(id) {
      return API.request(`/students/${id}`);
    },
    create(data) {
      return API.request("/students", { method: "POST", body: data });
    },
    update(id, data) {
      return API.request(`/students/${id}`, { method: "PUT", body: data });
    },
    delete(id) {
      return API.request(`/students/${id}`, { method: "DELETE" });
    }
  },

  projects: {
    getAll(params = {}) {
      const query = new URLSearchParams();
      if (params.search) query.append("search", params.search);
      if (params.category && params.category !== "all") query.append("category", params.category);
      if (params.status && params.status !== "all") query.append("status", params.status);
      if (params.tech) query.append("tech", params.tech);
      if (params.ownerId) query.append("ownerId", params.ownerId);

      const qs = query.toString();
      return API.request(`/projects${qs ? "?" + qs : ""}`);
    },
    getById(id) {
      return API.request(`/projects/${id}`);
    },
    create(data) {
      return API.request("/projects", { method: "POST", body: data });
    },
    update(id, data) {
      return API.request(`/projects/${id}`, { method: "PUT", body: data });
    },
    delete(id) {
      return API.request(`/projects/${id}`, { method: "DELETE" });
    }
  },

  requests: {
    getAll(params = {}) {
      const query = new URLSearchParams();
      if (params.ownerId) query.append("ownerId", params.ownerId);
      if (params.studentId) query.append("studentId", params.studentId);
      if (params.projectId) query.append("projectId", params.projectId);

      const qs = query.toString();
      return API.request(`/requests${qs ? "?" + qs : ""}`);
    },
    getById(id) {
      return API.request(`/requests/${id}`);
    },
    create(data) {
      return API.request("/requests", { method: "POST", body: data });
    },
    updateStatus(id, status) {
      return API.request(`/requests/${id}`, {
        method: "PUT",
        body: { status }
      });
    }
  },

  dashboard: {
    get(studentId) {
      const q = studentId ? `?studentId=${studentId}` : "";
      return API.request(`/dashboard${q}`);
    }
  },

  health: {
    check() {
      return API.request("/health");
    }
  }
};

// Global startup initialization
document.addEventListener("DOMContentLoaded", () => {
  UserStore.initUserSelector();
});
