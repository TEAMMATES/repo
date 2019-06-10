import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StatusMessageComponent } from './status-message.component';
import {StatusMessage} from "./status-message";

describe('StatusMessageComponent', () => {
  let component: StatusMessageComponent;
  let fixture: ComponentFixture<StatusMessageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StatusMessageComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatusMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  const messages: StatusMessage[] = [
    {message:'a', color:'black'},
    {message:'b', color:'red'},
    {message:'c', color:'green'},
  ];


  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should show all three messages', () => {
    component.messages = messages;
    fixture.detectChanges();
    const statusMessages: HTMLElement[] = fixture.nativeElement.querySelectorAll('div.alert div');
    expect(statusMessages.length).toEqual(3);
    expect(statusMessages[0].innerHTML).toEqual('a');
    expect(statusMessages[1].innerHTML).toEqual('b');
    expect(statusMessages[2].innerHTML).toEqual('c');
    expect(fixture).toMatchSnapshot();
  });

  it('should delete a message after clicking button', () => {
    component.messages = messages;
    fixture.detectChanges();
    const statusMessages: HTMLElement[] = fixture.nativeElement.querySelectorAll('div.alert div');
    const buttons: HTMLElement[] = fixture.nativeElement.querySelectorAll('div.alert button');
    expect(statusMessages.length).toEqual(3);
    expect(buttons.length).toEqual(3);
    buttons[0].click();
    fixture.detectChanges();
    const statusMessagesAfterClick: HTMLElement[] = fixture.nativeElement.querySelectorAll('div.alert div');
    expect(statusMessagesAfterClick.length).toEqual(2);
    expect(statusMessagesAfterClick[0].innerHTML).toEqual('b');
    expect(statusMessagesAfterClick[1].innerHTML).toEqual('c');
    buttons[1].click();
    buttons[2].click();
    fixture.detectChanges();
    const deleteAll: HTMLElement[] = fixture.nativeElement.querySelectorAll('div.alert div');
    expect(deleteAll.length).toEqual(0);
  });

});
