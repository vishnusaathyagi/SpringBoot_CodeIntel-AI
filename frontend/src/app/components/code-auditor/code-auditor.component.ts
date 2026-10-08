import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MarkdownComponent } from 'ngx-markdown';

@Component({
  selector: 'app-code-auditor',
  standalone: true,
  imports: [CommonModule, FormsModule, MarkdownComponent],
  templateUrl: './code-auditor.component.html',
  styleUrl: './code-auditor.component.css'
})
export class CodeAuditorComponent {
  language = signal<string>('Java'); // Default selected language
  context = signal<string>('Production REST Controller');
  codeSnippet = signal<string>('');
  aiOutput = signal<string>('');
  loading = signal<boolean>(false);

  async auditCode() {
    this.aiOutput.set('');
    this.loading.set(true);

    try {
      const response = await fetch('/api/v1/ai/review',
        {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          language: this.language(), // Selected language pass aagum
          context: this.context(),
          codeSnippet: this.codeSnippet()
        })
      });

      if (!response.body) return;

      const reader = response.body.getReader();
      const decoder = new TextDecoder();

      while (true) {
        const { value, done } = await reader.read();
        if (done) break;
        const chunk = decoder.decode(value, { stream: true });
        this.aiOutput.update(prev => prev + chunk);
      }
    } catch (err) {
      console.error(err);
      this.aiOutput.set('Error connecting to Spring Boot backend API.');
    } finally {
      this.loading.set(false);
    }
  }
}
