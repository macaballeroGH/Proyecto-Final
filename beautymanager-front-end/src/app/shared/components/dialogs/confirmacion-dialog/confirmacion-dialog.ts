import { Component } from "@angular/core";
import { MatDialogRef, MatDialogTitle, MatDialogContent, MatDialogActions } from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";


@Component({
  selector: 'app-confirmacion-dialog',
  imports: [MatDialogTitle, MatDialogContent, MatDialogActions, MatButtonModule],
  templateUrl: './confirmacion-dialog.html',
  styleUrl: './confirmacion-dialog.css',
})
export class ConfirmacionDialog {

  constructor(private dialogRef: MatDialogRef<ConfirmacionDialog>) {}

  cancelar(): void{
    this.dialogRef.close(false);
  }

  confirmar(): void{
    this.dialogRef.close(true);
  }
}
