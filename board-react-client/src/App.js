import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import Board from './Board';
import { instanceOf } from 'prop-types';
import { withCookies, Cookies } from 'react-cookie';

/*
function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}
export default App;
*/

class App extends Component {
  static propTypes = {
    cookies: instanceOf(Cookies).isRequired
  };

  static defaultProps = {
    menu1: true,
    menu2: false,
    boardType: 'board'
  }

  constructor(props) {
    super(props);
    const { cookies } = this.props;

    console.log("App constructor:"+cookies.get('menu1')+", "+cookies.get('menu2'));

    this.state = {
      menu1: (cookies.get('menu1') !== '') ? cookies.get('menu1') : this.props.menu1,
      menu2: (cookies.get('menu2') !== '') ? cookies.get('menu2') : this.props.menu2,
      boardType: (cookies.get('boardType') !== '') ? cookies.get('boardType') : this.props.boardType,
    }
    this.handleCreate = this.handleCreate.bind(this);
  }

  handleCreate(data) {
    console.log("App handleCreate:"+data.boardType);
    const { cookies } = this.props;

    cookies.set('menu1', data.menu1, { path: '/' });
    cookies.set('menu2', data.menu2, { path: '/' });
    cookies.set('boardType', data.boardType, { path: '/' });

    /* this.setState({
      menu1: data.menu1,
      menu2: data.menu2,
      boardType: data.boardType
    }); */
  }

  render() {
    return (
      <div><Board key={this.state.boardType} boardType={this.state.boardType} menu1={this.state.menu1} menu2={this.state.menu2} onCreate={this.handleCreate} /></div>      
    );
  }
}

export default withCookies(App);