import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'replaceNewlines',
  standalone: true
})
export class ReplaceNewlinesPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    return value ? value.replace(/\n/g, '<br/>') : '';
  }
}